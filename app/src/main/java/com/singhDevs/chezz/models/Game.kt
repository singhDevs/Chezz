package com.singhDevs.chezz.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Game(
    val id: String,
    val whitePlayer: GameUserModel,
    val whitePlayerId: String,
    val blackPlayer: GameUserModel,
    val blackPlayerId: String,
    val moves: String,
    val gameDuration: Int,
    val gameType: GameType,
    val gameMode: GameMode,
    val result: String,
    val winningUser: String,
    val termination: String,
    val whitePlayerRating: Int,
    val blackPlayerRating: Int
) : Parcelable

@Parcelize
data class GameUserModel(
    val username: String,
    val photoUrl: String?,
): Parcelable