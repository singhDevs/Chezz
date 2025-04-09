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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
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
    isTimerRunning: Boolean,
    onTimerStopped: () -> Unit
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            colorResource(R.color.timer_container1),
            colorResource(R.color.timer_container2)
        )
    )

    Card(
        border = BorderStroke(2.dp, colorResource(R.color.timer_container_border)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(40.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(15.dp, 5.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(isTimerRunning) TimerLottie()
                else TimerStoppedLottie()

                Text(
                    text = time,
                    fontSize = 18   .sp,
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
        modifier = Modifier.size(35.dp),
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
    TimerComposable("00:00", true, {})
}