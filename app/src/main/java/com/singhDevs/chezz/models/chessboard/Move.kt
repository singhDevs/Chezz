package com.singhDevs.chezz.models.chessboard

data class Move(
    val color: String,
    val flags: Int,
    val from: Int,
    val piece: String,
    val to: Int
)