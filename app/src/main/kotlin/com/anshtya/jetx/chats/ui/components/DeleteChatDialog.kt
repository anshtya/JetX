package com.anshtya.jetx.chats.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anshtya.jetx.R
import com.anshtya.jetx.common.ui.ComponentPreview

@Composable
fun DeleteChatDialog(
    chatCount: Int,
    onDismissRequest: () -> Unit,
    onConfirmClick: (deleteMedia: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var deleteMedia by rememberSaveable { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Delete " + if (chatCount > 1) "$chatCount chats?" else "this chat?")
        },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentSize()
            ) {
                Checkbox(
                    checked = deleteMedia,
                    onCheckedChange = { deleteMedia = it },
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(text = stringResource(id = R.string.delete_chat_dialog_text))
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmClick(deleteMedia) }) {
                Text(
                    text = stringResource(
                        id = if (chatCount > 1) R.string.delete_chat_dialog_confirm_multiple
                        else R.string.delete_chat_dialog_confirm_single
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.delete_chat_dialog_dismiss))
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
private fun DeleteChatDialogPreview() {
    ComponentPreview {
        DeleteChatDialog(
            chatCount = 10,
            onDismissRequest = {},
            onConfirmClick = {}
        )
    }
}