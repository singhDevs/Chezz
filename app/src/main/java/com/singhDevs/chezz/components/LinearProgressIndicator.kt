package com.singhDevs.chezz.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.singhDevs.chezz.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LinearProgressIndicator(
    modifier: Modifier,
    indicatorProgress: Float,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
//    var progress by remember { mutableFloatStateOf(0F) }
    val progressAnimDuration = 1_500
    val target = (indicatorProgress / 3f).coerceIn(0f, 1f)

    val progressAnimation by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = progressAnimDuration, easing = FastOutSlowInEasing),
    )
    LinearProgressIndicator(
        progress = { progressAnimation },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        color = colorResource(R.color.primary_amber),
        trackColor = colorResource(R.color.secondary_dark)
    )
//    LaunchedEffect(lifecycleOwner) {
//        progress = indicatorProgress
//    }
}