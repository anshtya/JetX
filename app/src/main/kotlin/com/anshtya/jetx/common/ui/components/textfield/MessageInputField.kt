package com.anshtya.jetx.common.ui.components.textfield

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.anshtya.jetx.R

@Composable
fun MessageInputField(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = inputText,
        onValueChange = onInputTextChange,
        placeholder = {
            Text(text = stringResource(id = R.string.chatinputfield_placeholder))
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Send
        ),
        keyboardActions = KeyboardActions {
            if (inputText.isNotBlank()) {
                onMessageSent(inputText)
                onInputTextChange("")
            }
        },
        modifier = modifier.clip(CircleShape)
    )
}