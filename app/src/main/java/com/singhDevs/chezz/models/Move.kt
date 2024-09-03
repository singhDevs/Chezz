package com.singhDevs.chezz.models

data class Move(
    val from: String,
    val to: String,
    val piece: String = "p"
)