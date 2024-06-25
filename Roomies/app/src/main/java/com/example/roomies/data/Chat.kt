package com.example.roomies.data

data class Chat(
    val id: String? = null,
    val name: String? = null,
    val text: String? = null,
    val time: String? = null
)
data class GroupChat(
    val chat: MutableList<Chat>? = null
)