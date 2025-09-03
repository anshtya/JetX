package com.anshtya.jetx.send

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshtya.jetx.R
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.core.ui.UserListItem
import com.anshtya.jetx.core.ui.components.button.BackButton
import com.anshtya.jetx.core.ui.components.button.SendButton
import com.anshtya.jetx.core.ui.components.scaffold.JetxScaffold
import com.anshtya.jetx.core.ui.components.topappbar.JetxTopAppBar
import com.anshtya.jetx.core.ui.noRippleClickable

@Composable
fun SendScreen(
    onNavigateUp: () -> Unit,
    onActivityFinish: () -> Unit,
    onSend: (Set<Int>) -> Unit,
    viewModel: SendViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) onActivityFinish()
    }

    val recipients by viewModel.recipients.collectAsStateWithLifecycle()
    val selectedChatIds by viewModel.selectedChatIds.collectAsStateWithLifecycle()

    JetxScaffold(
        topBar = {
            JetxTopAppBar(
                title = {
                    Text(text = stringResource(R.string.send_to))
                },
                navigationIcon = { BackButton(onNavigateUp) }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = selectedChatIds.isNotEmpty(),
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                SendBottomAppBar(
                    selectedChatIds = selectedChatIds,
                    recipients = recipients,
                    onSend = { onSend(selectedChatIds) }
                )
            }
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (recipients.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.recent_chats),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                items(
                    items = recipients,
                    key = { it.id }
                ) { member ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .noRippleClickable { viewModel.addOrRemoveChatId(member.id) }
                    ) {
                        UserListItem(
                            profilePictureUrl = member.profilePicture,
                            username = member.username,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = selectedChatIds.contains(member.id),
                            onCheckedChange = null
                        )
                    }
                }
            } else {
                item {
                    Box(Modifier.fillMaxSize()) {
                        Text(
                            text = stringResource(id = R.string.no_recent_chats),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SendBottomAppBar(
    selectedChatIds: Set<Int>,
    recipients: List<Recipient>,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(modifier) {
        Row(Modifier.weight(1f)) {
            selectedChatIds.forEachIndexed { index, chatId ->
                val name = recipients.find { recipient -> recipient.id == chatId }?.username
                Text(
                    buildAnnotatedString {
                        append(name)
                        if (index + 1 < selectedChatIds.size) append(", ")
                    }
                )
            }
        }
        SendButton(onClick = onSend)
    }
}