package com.singhDevs.chezz.components

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.singhDevs.chezz.R
import com.singhDevs.chezz.activities.ReplayGameActivity
import com.singhDevs.chezz.models.Game
import com.singhDevs.chezz.models.GameMode
import com.singhDevs.chezz.models.GameType

@Composable
fun GameHistoryComposable(context: Context, game: Game, token: String, username: String) {
    val surfaceColor = Color(0xFF1E1E1E)
//    val red = Color(0xFFFF6666).copy(alpha = 0.4f)
//    val green = Color(0xFF66FF99).copy(alpha = 0.4f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceColor, shape = CircleShape)
            .clickable {
                val intent = Intent(context, ReplayGameActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("token", token)
                intent.putExtra("game", game)
                context.startActivity(intent)
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            when (game.gameMode) {
                                GameMode.RATED -> colorResource(R.color.bg_rated).copy(alpha = 0.75f)
                                GameMode.CASUAL -> colorResource(R.color.bg_casual).copy(alpha = 0.75f)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .size(30.dp)
                            .padding(5.dp),
                        painter =
                            when (game.gameType) {
                                GameType.RAPID -> painterResource(R.drawable.ic_rapid)
                                GameType.BLITZ -> painterResource(R.drawable.ic_blitz)
                                GameType.BULLET -> painterResource(R.drawable.ic_bullet)
                            },
                        contentDescription = null
                    )
                }
                Row(
                    modifier = Modifier.padding(start = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val photoUrl =
                        if (username != game.whitePlayer.username) game.whitePlayer.photoUrl
                        else game.blackPlayer.photoUrl

                    AsyncImage(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(percent = 20))
                            .border(1.dp, Color.Gray, RoundedCornerShape(percent = 20)),
                        model = photoUrl,
                        contentDescription = null,
                        error = painterResource(R.drawable.pfp_unavailable)
                    )
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text =
                            if (game.whitePlayer.username == username)
                                game.blackPlayer.username
                            else
                                game.whitePlayer.username,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                        color = Color.White
                    )
                }
            }

            Log.d("GameHistoryComposable", "game.winningUser: $game.winningUser")
            Image(
                modifier = Modifier.size(25.dp),
                painter = painterResource(
                    if (game.winningUser == username) R.drawable.ic_win
                    else if (game.result == "DRAW") R.drawable.ic_draw
                    else R.drawable.ic_minus
                ),
                contentDescription = null
            )
        }
    }
}