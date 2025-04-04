package com.singhDevs.chezz.models

data class GameOverResponse(
    val id: String,
    val type: String,
    val move: Move,
    val board: Any,
    val result: ResultType,
    val cause: String,
    val winningUser: String,
    val updatedRatings: Ratings?
)