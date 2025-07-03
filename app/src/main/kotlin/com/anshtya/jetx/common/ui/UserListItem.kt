package com.anshtya.jetx.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserListItem(
    profilePictureUrl: String?,
    username: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        ProfilePicture(
            model = profilePictureUrl,
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterVertically)
        )

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = username,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 18.sp
            )
            supportingText?.let {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = supportingText,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}