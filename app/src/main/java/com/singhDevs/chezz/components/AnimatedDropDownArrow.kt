package com.singhDevs.chezz.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedDropdownArrow(expanded: Boolean) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    Icon(
        Icons.Rounded.ArrowDropDown,
        contentDescription = null,
        modifier = Modifier
            .padding(start = 8.dp)
            .size(30.dp)
            .graphicsLayer(
                rotationZ = rotationAngle,
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            )
    )
}
