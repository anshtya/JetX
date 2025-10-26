package com.anshtya.jetx.core.ui.components.textfield

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.anshtya.jetx.R

@Composable
fun UsernameTextField(
    username: TextFieldValue,
    usernameError: String?,
    usernameValid: Boolean,
    onUsernameChange: (TextFieldValue) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    TextField(
        value = username,
        onValueChange = onUsernameChange,
        enabled = enabled,
        placeholder = {
            Text(text = stringResource(id = R.string.username))
        },
        singleLine = true,
        isError = isError,
        supportingText = {
            Text(text = usernameError ?: "")
        },
        trailingIcon = {
            if (usernameValid) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = Color.Green,
                    contentDescription = null
                )
            }
        },
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )
}