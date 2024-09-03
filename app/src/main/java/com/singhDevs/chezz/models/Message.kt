package com.singhDevs.chezz.models

data class Message(
    val type: String,
    val color: String? = null,
    val move: Move? = null,
    val board: Any? = null,
    val result: Any? = null
)