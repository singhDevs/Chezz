package com.singhDevs.chezz.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.singhDevs.chezz.R

@Composable
fun TimerComposable(
    time: String,
    isTimerPaused: Boolean,
    isGameOver: Boolean
) {
    val darkBackground = Color(0xFF1A1A1A)
    val darkerBackground = Color(0xFF121212)
    val primaryAmber = Color(0xFFA6771E)
    val textColor = Color(0xFFE0E0E0)
    val fieldBackground = Color(0xFF2A2A2A)
    val accentBrown = Color(0xFF94551A)
    val activeGradient = Brush.horizontalGradient(
        colors = listOf(
            primaryAmber,
            accentBrown
        )
    )
    val passiveGradient = Brush.horizontalGradient(
        colors = listOf(
            fieldBackground,
            fieldBackground
        )
    )

    Card(
        border = BorderStroke(1.dp, colorResource(R.color.timer_container_border)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .background(if(isTimerPaused) passiveGradient else activeGradient)
                .padding(12.dp, 5.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(isTimerPaused || !isGameOver) TimerLottie()
                else TimerStoppedLottie()

                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(R.color.timer_text)
                )
            }
        }
    }
}

@Composable
fun TimerLottie() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.timer))
    LottieAnimation(
        modifier = Modifier.size(30.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
}

@Composable
fun TimerStoppedLottie() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.timer))
    LottieAnimation(
        modifier = Modifier.size(35.dp),
        composition = composition,
        iterations = 1
    )
}

@Preview
@Composable
private fun TimerComposablePreview() {
    TimerComposable(time = "00:00", isTimerPaused = true, isGameOver = false)
}