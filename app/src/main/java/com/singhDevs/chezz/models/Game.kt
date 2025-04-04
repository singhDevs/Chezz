package com.singhDevs.chezz.models

import java.io.Serializable

data class Game(
    val id: String,
    val whitePlayer: User,
    val whitePlayerId: String,
    val blackPlayer: User,
    val blackPlayerId: String,
    val moves: String,
    val gameDuration: Int,
    val gameType: GameType,
    val result: String,
    val winningUser: String,
    val termination: String,
) : Serializable