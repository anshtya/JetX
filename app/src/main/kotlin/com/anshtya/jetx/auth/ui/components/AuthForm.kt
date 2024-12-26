package com.anshtya.jetx.auth.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.anshtya.jetx.R

@Composable
fun AuthForm(
    email: String,
    password: String,
    passwordVisible: Boolean,
    authButtonEnabled: Boolean,
    authButtonText: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onAuthButtonClick: () -> Unit,
    emailError: String?,
    passwordError: String?
) {
    val focusManager = LocalFocusManager.current

    Column {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            enabled = authButtonEnabled,
            placeholder = {
                Text(text = stringResource(id = R.string.email))
            },
            singleLine = true,
            isError = emailError != null && authButtonEnabled,
            supportingText = {
                Text(text = emailError ?: "")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            enabled = authButtonEnabled,
            placeholder = {
                Text(text = stringResource(id = R.string.password))
            },
            singleLine = true,
            isError = passwordError != null && authButtonEnabled,
            supportingText = {
                Text(text = passwordError ?: "")
            },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = onPasswordVisibilityChange
                ) {
                    if (passwordVisible) {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = stringResource(id = R.string.hide_password),
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = stringResource(id = R.string.show_password),
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = {
                focusManager.clearFocus()
                onAuthButtonClick()
            },
            enabled = authButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = authButtonText)
        }
    }
}