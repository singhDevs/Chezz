package com.singhDevs.chezz.models

data class GameOverResponse(
    val id: String,
    val type: String,
    val move: Move,
    val board: Any,
    val lastPlayerToMove: String,   // Will get the ID of the player who made the last move
    val result: ResultType,
    val cause: String,
    val winningUser: String,
    val updatedRatings: Ratings?
)