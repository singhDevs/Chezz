package com.singhDevs.chezz.models.chessboard

data class History(
    val castling: Castling,
    val epSquare: Int,
    val halfMoves: Int,
    val kings: Kings,
    val move: Move,
    val moveNumber: Int,
    val turn: String
)