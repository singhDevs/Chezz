package com.singhDevs.chezz.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Message(
    val type: String,
    val color: Char? = null,
    val piece: String? = null,
    val opponent: UserWithoutCreds? = null,
    val move: Move? = null,
    val board: Any? = null,
    val turn: Char? = null,
    val whiteTime: Long? = null,
    val blackTime: Long? = null,
    val result: ResultType? = null,
    val cause: String? = null,
    val onlineUsers: Int? = null,
    val duration: Int? = null,
    val gameType: GameType? = null
)

@Parcelize
data class UserWithoutCreds(
    val id: String,
    val username: String,
    val photoUrl: String?,
    val ratings: Ratings?
): Parcelable