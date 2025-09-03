package com.anshtya.jetx.core.model.sampledata

import com.anshtya.jetx.core.model.UserProfile
import java.util.UUID

val sampleUsers = listOf(
    UserProfile(
        id = UUID.fromString("1"),
        name = "Alice Johnson",
        username = "alice_j",
        pictureUrl = null
    ),
    UserProfile(
        id = UUID.fromString("2"),
        name = "Bob Carter",
        username = "bobcat99",
        pictureUrl = null
    )
)