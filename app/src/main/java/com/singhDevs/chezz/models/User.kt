package com.singhDevs.chezz.models

import java.io.Serializable

data class User(
    val username: String,
    val bulletRating: Int,
    val blitzRating: Int,
    val rapidRating: Int,
    val photoUrl: String?
) : Serializable