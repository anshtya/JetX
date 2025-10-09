package com.anshtya.jetx.core.model.sampledata

import com.anshtya.jetx.core.model.UserProfile
import java.util.UUID

val sampleUsers = listOf(
    UserProfile(
        id = UUID.randomUUID(),
        name = "Alice Johnson",
        username = "alice_j",
        phoneNumber = "number1",
        pictureUrl = null
    ),
    UserProfile(
        id = UUID.randomUUID(),
        name = "Bob Carter",
        username = "bobcat99",
        phoneNumber = "number2",
        pictureUrl = null
    )
)