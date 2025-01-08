package com.anshtya.jetx.chats.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.anshtya.jetx.common.model.UserProfile
import com.anshtya.jetx.common.ui.ComponentPreview
import com.anshtya.jetx.common.ui.DayNightPreview
import com.anshtya.jetx.common.ui.ProfilePicture
import java.util.UUID

@Composable
fun SearchItem(
    userProfile: UserProfile,
    onClick: (UserProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(userProfile) }
    ) {
        ProfilePicture(
            model = userProfile.pictureUrl,
            onClick = {
                // TODO: add profile view
            },
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterVertically)
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = userProfile.username,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = userProfile.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@DayNightPreview
@Composable
private fun ChatItemPreview() {
    ComponentPreview {
        SearchItem(
            userProfile = UserProfile(
                id = UUID.fromString("id"),
                name = "name",
                username = "username",
                pictureUrl = null
            ),
            onClick = {}
        )
    }
}