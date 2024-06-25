package com.example.roomies.data

data class Group(
    var id: String? = null,
    var name: String? = null,
    var owner: String? = null,
    var members: MutableList<String>? = null,
)