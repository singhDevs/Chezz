package com.singhDevs.chezz.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
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
fun LoadingDialog(modifier: Modifier = Modifier, onlineUsers: Int) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.chess_loading))
    Column(
        modifier = modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            modifier = Modifier.size(120.dp),
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        Text(
            text = "Finding opponent...",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = colorResource(R.color.text_light_square)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 45.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.size(15.dp),
                painter = painterResource(R.drawable.ic_online),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = "$onlineUsers currently online",
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                color = colorResource(R.color.text_light_square)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoadingDialogPreview() {
    LoadingDialog(Modifier, 1)
}