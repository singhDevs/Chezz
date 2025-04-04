package com.singhDevs.chezz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singhDevs.chezz.R
import com.singhDevs.chezz.models.GameMode

@Composable
fun GameModeComposable(onGameModeSelected: (gameMode: GameMode) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Rated or Casual?",
            fontWeight = FontWeight.Thin,
            fontSize = 25.sp,
            color = Color.White
        )
        Row(
            modifier = Modifier
                .padding(top = 22.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.bg_rated_btn)
                ),
                shape = RoundedCornerShape(percent = 20),
                onClick = { onGameModeSelected(GameMode.RATED) }
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .background(colorResource(R.color.bg_rated_btn)),
                    text = "Rated",
                    fontSize = 20.sp,
                    color = colorResource(R.color.bg_rated_txt)
                )
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.bg_casual_btn)
                ),
                shape = RoundedCornerShape(percent = 20),
                onClick = { onGameModeSelected(GameMode.CASUAL) }
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .background(colorResource(R.color.bg_casual_btn)),
                    text = "Casual",
                    fontSize = 18.sp,
                    color = colorResource(R.color.bg_casual_txt)
                )
            }
        }
    }
}

@Preview
@Composable
private fun GameModeComposablePreview() {
    GameModeComposable{}
}