package com.singhDevs.chezz.models

data class Message(
    val type: String,
    val color: Char? = null,
    val piece: String? = null,
    val opponent: String? = null,
    val move: Move? = null,
    val board: Any? = null,
    val turn: Char? = null,
    val whiteTime: Long? = null,
    val blackTime: Long? = null,
    val result: Char? = null,
    val cause: String? = null,
    val onlineUsers: Int? = null
)