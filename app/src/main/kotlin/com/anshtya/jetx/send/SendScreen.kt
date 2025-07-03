package com.anshtya.jetx.send

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.anshtya.jetx.common.ui.BackButton
import com.anshtya.jetx.common.ui.SendButton
import com.anshtya.jetx.common.ui.UserListItem
import com.anshtya.jetx.common.ui.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendScreen(
    sendViewModel: SendViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onSend: (Set<Int>) -> Unit,
) {
    val members by sendViewModel.members.collectAsStateWithLifecycle()
    val selectedChatIds by sendViewModel.selectedChatIds.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
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
                BottomAppBar {
                    Row(Modifier.weight(1f)) {
                        selectedChatIds.forEachIndexed { index, chatId ->
                            val name = members.find { member -> member.id == chatId }?.username
                            Text(
                                buildAnnotatedString {
                                    append(name)
                                    if (index+1 < selectedChatIds.size) append(", ")
                                }
                            )
                        }
                    }
                    SendButton(onClick = { onSend(selectedChatIds)})
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (members.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.recent_chats),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    items(
                        items = members,
                        key = { it.id }
                    ) { member ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .noRippleClickable { sendViewModel.addOrRemoveChatId(member.id) }
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
}