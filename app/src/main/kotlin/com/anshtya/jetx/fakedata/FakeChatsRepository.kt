package com.anshtya.jetx.fakedata

//class FakeChatsRepository @Inject constructor() : ChatsRepository {
//    private val _chats = MutableStateFlow(fakeChats)
//    override val chats: StateFlow<List<Chat>> = _chats.asStateFlow()
//
//    override fun deleteChat(id: Int): Int {
//        _chats.update {
//            it.toMutableList().apply {
//                removeIf { chat -> chat.id == id }
//            }
//        }
//        return id
//    }
//}