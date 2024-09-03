package com.singhDevs.chezz.utils

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat

object BasicUtils {
    fun getColor(context: Context, colorId: Int): Color = Color(ContextCompat.getColor(context, colorId))
}