package com.anshtya.jetx.chats.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anshtya.jetx.R
import com.anshtya.jetx.common.ui.BackButton

@Composable
fun SearchTextField(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSearchDisable: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = inputText,
        onValueChange = onInputTextChange,
        placeholder = {
            Text(text = stringResource(id = R.string.search))
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        leadingIcon = { BackButton { onSearchDisable() } },
        trailingIcon = {
            if (inputText.isNotBlank() || inputText.isNotEmpty()) {
                IconButton(onClick = { onInputTextChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_search)
                    )
                }
            }
        },
        modifier = modifier
            .size(54.dp)
            .clip(CircleShape)
    )
}