# Architecture

## Package Structure

```
com.anshtya.jetx/
├── core/
│   ├── database/          # Room database — entities, DAOs, data sources
│   ├── network/           # Retrofit APIs, WebSocket, services, DI modules
│   ├── preferences/       # DataStore and EncryptedSharedPreferences
│   ├── model/             # Domain models (Chat, UserProfile, MessageType)
│   ├── ui/                # Shared Composable components
│   └── coroutine/         # Coroutine dispatcher providers
├── auth/                  # Authentication (login, register, logout)
├── profile/               # User profile creation and management
├── chats/                 # Chat list and messaging
├── attachments/           # File and image handling
├── notifications/         # Notification management
├── work/                  # Background workers (WorkManager)
├── fcm/                   # Firebase Cloud Messaging
├── s3/                    # S3 cloud storage integration
├── camera/                # Camera capture
├── calls/                 # Call logs
├── send/                  # Send/share functionality
├── settings/              # User settings
├── onboarding/            # Onboarding flow
├── registration/          # Registration flow
└── ui/
    ├── app/               # App-level navigation and AppViewModel
    └── main/              # Main screen navigation
```

---

## Data Layer

### Network

Two `Retrofit` instances are provided via Hilt — `@Base` (unauthenticated, for auth endpoints) and `@Authenticated` (includes `AuthInterceptor` to attach tokens and `AuthAuthenticator` to refresh on 401). Each API is wrapped in a service class using `safeApiCall()`, which normalises responses into a `NetworkResult<T>` sealed class.

`WebSocketManager` maintains a persistent connection, monitors network state, and reconnects automatically. Incoming frames are dispatched to `WebsocketMessageProcessor` on the `Default` dispatcher, which routes them to the repository layer.

### Database

Room database with entities for messages, chats, user profiles, attachments, and a `RecentMessageEntity` used to efficiently power chat list queries. `LocalMessagesDataSource` wraps the DAOs and runs multi-table writes (insert message + create chat if absent + upsert recent message) inside a single `db.withTransaction`.

### Preferences

`JetxPreferencesStore` combines three stores: tokens in `EncryptedSharedPreferences`, and account/user state in `DataStore`. `AuthManager` owns an `AuthState` `StateFlow` derived from the token store and drives app-level navigation.

### Repositories

Each feature (auth, profile, chats, messages) has an interface + implementation pair bound via Hilt. Repositories are the single source of truth — they coordinate between the network services and the local database, so the rest of the app never talks to either directly.

- **Auth** — calls the network service, then stores or clears the session in `AuthManager`.
- **Profile** — writes to both the remote API and local DB; exposes profile data as a `Flow` from the DB.
- **Chats** — reads from the local DB only; remote sync is driven by the messaging layer.
- **Messages** — narrow data access only: read messages from the DB, insert a message, mark it received/seen on the server. Sending/receiving orchestration lives in the domain layer (see below).

---

## Domain Layer

Multi-step flows that span more than one repository (or a repository plus infra like `WorkManager` or notifications) live in `<feature>/domain/` as use cases — plain classes with a single `operator fun invoke(...)`, constructor-injected via Hilt like anything else. They exist to keep repositories narrow (one repository = one data type = one source of truth) instead of letting one repository reach into others.

A ViewModel that only needs a single repository call still talks to the repository directly — a use case is introduced only when a ViewModel would otherwise have to coordinate several repositories/managers itself.

- **`SendChatMessageUseCase`** (`chats/domain/`) — migrates an attachment into app storage, saves the sender's profile, inserts the message into `MessagesRepository`, and schedules `MessageSendWorker`. Coordinates `MessagesRepository`, `ChatsRepository`, `ProfileRepository`, `AttachmentRepository`, `AuthManager`, and `WorkManager`.
- **`ReceiveChatMessageUseCase`** (`chats/domain/`) — fetches attachment metadata for an incoming message, saves the sender's profile, inserts the message, acknowledges receipt to the server, and posts a notification. Coordinates `MessagesRepository`, `ProfileRepository`, `AttachmentRepository`, and `DefaultNotificationManager`.

Both are called from every entry point that can send/receive a message — `ChatViewModel`, `MediaPreviewViewModel`, and `ReplyReceiver` call `SendChatMessageUseCase`; `WebsocketMessageProcessor` and `MessageReceiveWorker` call `ReceiveChatMessageUseCase` — so the orchestration logic exists in exactly one place instead of being duplicated per caller.

---

## Data Flow

### Sending a Message

```
ChatScreen
  ▼  user taps send
ChatViewModel
  ▼
SendChatMessageUseCase
  ├─ migrate attachment to app-internal storage
  ├─ insert message into DB via MessagesRepository (status = SENDING)  ← UI updates immediately
  └─ schedule MessageSendWorker
       ▼
     Retrofit → server
       ▼  on success
     update message status in DB (SENDING → RECEIVED)  ← UI updates
```

The recipient receives the message via WebSocket, replies with a `MESSAGE_UPDATE`, which flips the status to SEEN.

### Receiving a Message (WebSocket)

```
WebSocketManager
  ▼  NEW_MESSAGE frame
WebsocketMessageProcessor
  ▼
ReceiveChatMessageUseCase
  ├─ fetch attachment metadata from API (if any)
  ├─ insert message into DB via MessagesRepository  ← UI updates via Flow
  ├─ mark message received on server
  └─ post notification

  ▼  MESSAGE_UPDATE frame
WebsocketMessageProcessor
  └─ update message status in DB  ← UI updates via Flow
```

### Loading the Chat List

```
ChatListViewModel
  _selectedFilter (MutableStateFlow)
    └─ flatMapLatest
         ChatsRepository.getChats(filter)  ← Flow<List<Chat>> from DB
           └─ stateIn(WhileSubscribed(5000))
                ▼
              chatList StateFlow  →  ChatListScreen recomposes
```

---

## Tech Stack

| Concern | Library |
|---|---|
| UI | Jetpack Compose |
| Networking | Retrofit + OkHttp |
| Real-time | OkHttp WebSocket |
| Local storage | Room + DataStore |
| Secure storage | EncryptedSharedPreferences (AES256-GCM) |
| Serialization | Kotlinx Serialization |
| DI | Hilt / Dagger |
| Concurrency | Kotlin Coroutines + Flow |
| Background work | WorkManager |
| Push notifications | Firebase Cloud Messaging |
| Image loading | Coil |
| Video playback | Media3 |
| Cloud storage | AWS S3 |
