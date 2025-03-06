package com.singhDevs.chezz.utils

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat

object BasicUtils {
    fun getColor(context: Context, colorId: Int): Color = Color(ContextCompat.getColor(context, colorId))
    fun millisToString(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    fun generateMoveString(from: String, to: String, piece: Char, color: Char) = "$color$piece$from$to"
}