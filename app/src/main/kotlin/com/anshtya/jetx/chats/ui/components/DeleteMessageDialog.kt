package com.anshtya.jetx.chats.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anshtya.jetx.R
import com.anshtya.jetx.common.ui.ComponentPreview

@Composable
fun DeleteMessageDialog(
    messageCount: Int,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Delete " + if (messageCount > 1) "$messageCount messages?" else "message?")
        },
        confirmButton = {
            TextButton(onClick = { onConfirmClick() }) {
                Text(text = stringResource(id = R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.delete_dialog_dismiss))
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
private fun DeleteChatDialogPreview() {
    ComponentPreview {
        DeleteMessageDialog(
            messageCount = 10,
            onDismissRequest = {},
            onConfirmClick = {}
        )
    }
}