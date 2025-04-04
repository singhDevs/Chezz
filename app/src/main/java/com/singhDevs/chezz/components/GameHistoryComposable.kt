package com.singhDevs.chezz.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.singhDevs.chezz.R
import com.singhDevs.chezz.models.Game
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.models.User

@Composable
fun GameHistoryComposable(game: Game, username: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.secondary_dark))
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

                Image(
                    modifier = Modifier.size(20.dp),
                    painter =
                    when(game.gameType){
                        GameType.RAPID -> painterResource(R.drawable.ic_rapid)
                        GameType.BLITZ -> painterResource(R.drawable.ic_blitz)
                        GameType.BULLET -> painterResource(R.drawable.ic_bullet)
                    },
                    contentDescription = null
                )
                Row(
                    modifier = Modifier.padding(start = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val photoUrl =
                        if (username != game.whitePlayer.username) game.whitePlayer.photoUrl
                        else game.blackPlayer.photoUrl

                    AsyncImage(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(RoundedCornerShape(percent = 20))
                            .border(1.dp, Color.Gray, RoundedCornerShape(percent = 20)),
                        model = photoUrl,
                        contentDescription = null,
                        error = painterResource(R.drawable.pfp_unavailable)
                    )
                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text =
                        if (game.whitePlayer.username == username)
                            game.blackPlayer.username
                        else
                            game.whitePlayer.username,
                        fontSize = 23.sp,
                        color = Color.White
                    )
                }
            }
            Image(
                modifier = Modifier.size(25.dp),
                painter = painterResource(
                    if (game.winningUser == username) R.drawable.ic_win
                    else if (game.result == "d") R.drawable.ic_draw
                    else R.drawable.ic_minus
                ),
                contentDescription = null
            )
        }
    }
}