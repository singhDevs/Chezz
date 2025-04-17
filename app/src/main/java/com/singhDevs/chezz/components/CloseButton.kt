package com.singhDevs.chezz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CloseButton(modifier: Modifier = Modifier, onDismiss: () -> Unit) {
    val textColor = Color(0xFFE0E0E0)
    val fieldBackground = Color(0xFF2A2A2A)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(fieldBackground)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = textColor
            )
        }
    }
}