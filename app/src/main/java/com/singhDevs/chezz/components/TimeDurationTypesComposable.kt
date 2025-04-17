package com.singhDevs.chezz.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.singhDevs.chezz.R
import com.singhDevs.chezz.models.Game
import com.singhDevs.chezz.models.GameMode
import com.singhDevs.chezz.models.GameType

@Composable
fun TimeDurationTypesComposable(
    gameMode: GameMode,
    onTimeSelected: (gameDuration: Int, gameType: GameType) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "How much time do you have?",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Light),
            color = Color.White
        )
        Row(
            modifier = Modifier.padding(top = 25.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(25.dp),
                painter = painterResource(R.drawable.ic_bullet),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = "Bullet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Light
            )
        }
        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                shape = RoundedCornerShape(percent = 20),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                    when (gameMode) {
                        GameMode.RATED -> colorResource(R.color.bg_rated_btn)
                        GameMode.CASUAL -> colorResource(R.color.bg_casual_btn)
                    },
                    contentColor = when (gameMode) {
                        GameMode.RATED -> colorResource(R.color.bg_rated_txt)
                        GameMode.CASUAL -> colorResource(R.color.bg_casual_txt)
                    }
                ),
                onClick = { onTimeSelected(1 * 60 * 1000, GameType.BULLET) }
            ) {
                Text(
                    modifier = Modifier
                        .padding(10.dp)
                        .background(
                            when (gameMode) {
                                GameMode.RATED -> colorResource(R.color.bg_rated_btn)
                                GameMode.CASUAL -> colorResource(R.color.bg_casual_btn)
                            }
                        ),
                    text = "1 min",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Row(
            modifier = Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(25.dp),
                painter = painterResource(R.drawable.ic_blitz),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = "Blitz",
                fontSize = 20.sp,
                fontWeight = FontWeight.Light
            )
        }
        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(3, 5).forEach { minutes ->
                Button(
                    modifier = Modifier
                        .padding(end = 20.dp),
                    shape = RoundedCornerShape(percent = 20),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                        when (gameMode) {
                            GameMode.RATED -> colorResource(R.color.bg_rated_btn)
                            GameMode.CASUAL -> colorResource(R.color.bg_casual_btn)
                        },
                        contentColor = when (gameMode) {
                            GameMode.RATED -> colorResource(R.color.bg_rated_txt)
                            GameMode.CASUAL -> colorResource(R.color.bg_casual_txt)
                        }
                    ),
                    onClick = { onTimeSelected(minutes * 60 * 1000, GameType.BLITZ) }
                ) {
                    Text(
                        text = "$minutes min",
                        modifier = Modifier
                            .padding(10.dp)
                            .background(
                                when (gameMode) {
                                    GameMode.RATED -> colorResource(R.color.bg_rated_btn)
                                    GameMode.CASUAL -> colorResource(R.color.bg_casual_btn)
                                }
                            ),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(25.dp),
                painter = painterResource(R.drawable.ic_rapid),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = "Rapid",
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
            )
        }
        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(10, 30).forEach { minutes ->
                Button(
                    modifier = Modifier
                        .padding(end = 20.dp),
                    shape = RoundedCornerShape(percent = 20),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                        when (gameMode) {
                            GameMode.RATED -> colorResource(R.color.bg_rated_btn)
                            GameMode.CASUAL -> colorResource(R.color.bg_casual_btn)
                        },
                        contentColor = when (gameMode) {
                            GameMode.RATED -> colorResource(R.color.bg_rated_txt)
                            GameMode.CASUAL -> colorResource(R.color.bg_casual_txt)
                        }
                    ),
                    onClick = { onTimeSelected(minutes * 60 * 1000, GameType.RAPID) }
                ) {
                    Text(
                        text = "$minutes min",
                        modifier = Modifier
                            .padding(10.dp)
                            .background(
                                when (gameMode) {
                                    GameMode.RATED -> colorResource(R.color.bg_rated_btn)
                                    GameMode.CASUAL -> colorResource(R.color.bg_casual_btn)
                                }
                            ),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun TimeDurationTypesComposablePreview() {
    TimeDurationTypesComposable(GameMode.CASUAL) { gameDuration, gameType ->
    }
}