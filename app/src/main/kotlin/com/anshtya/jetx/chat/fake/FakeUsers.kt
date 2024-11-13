package com.anshtya.jetx.chat.fake

import androidx.annotation.DrawableRes

val fakeUsers = listOf(
    User(
        id = 1,
        name = "Alice Johnson",
        username = "alice_j",
        bio = "",
        photo = null
    ),
    User(
        id = 2,
        name = "Bob Carter",
        username = "bobcat99",
        bio = "",
        photo = null
    ),
    User(
        id = 3,
        name = "John Doe",
        username = "john_doe",
        bio = "",
        photo = null
    )
)

data class User(
    val id: Int,
    val name: String,
    val username: String,
    @DrawableRes val photo: Int?,
    val bio: String
)