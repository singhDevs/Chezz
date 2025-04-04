package com.singhDevs.chezz.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ratings(
    val bulletRating: Int = 1500,
    val blitzRating: Int = 1500,
    val rapidRating: Int = 1500
) : Parcelable