package com.singhDevs.chezz.components

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.singhDevs.chezz.R
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.models.ResultType
import com.singhDevs.chezz.models.UserWithoutCreds
import com.singhDevs.chezz.viewmodels.ChessBoardViewModel

private const val TAG = "ResultDialog"

@Composable
fun ResultDialog(
    context: Context,
    result: ResultType,
    cause: String,
    newRating: Int?,
    oldRating: Int?,
    gameType: GameType,
    gameDuration: Int,
    playerWhite: UserWithoutCreds,
    playerBlack: UserWithoutCreds,
    winningUsername: String,
    username: String,
    viewModel: ChessBoardViewModel,
    onDismissRequest: () -> Unit,
    onViewOpponentProfileClicked: () -> Unit,
    onNewGameClicked: () -> Unit,
    onExportPGNClicked: (toggleProgressIndicator: () -> Unit) -> Unit,
) {
    if (newRating != null) viewModel.updateRating(gameType, newRating)
    var pgnBtnClicked by remember { mutableStateOf(false) }

    val textColor = Color(0xFFE0E0E0)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(15.dp)
        ) {
            Box(
                modifier = Modifier.background(colorResource(R.color.surface))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp, 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text =
                                    if (result == ResultType.DRAW) "Draw"
                                    else if (winningUsername == username) "You Won!"
                                    else "You Lost",
                                style = MaterialTheme.typography.headlineLarge,
                                color =
                                    if (result == ResultType.DRAW) colorResource(R.color.text_secondary)
                                    else if (winningUsername == username) colorResource(R.color.golden)
                                    else colorResource(R.color.you_lost_text)
                            )
                            Text(
                                text = cause,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorResource(R.color.text_secondary)
                            )
                        }
                        CloseButton(
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .size(30.dp)
                                .align(Alignment.TopEnd),
                            onDismiss = onDismissRequest
                        )
                    }


                    //Logo and usernames
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            modifier = Modifier.size(150.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = playerWhite.photoUrl,
                                error = painterResource(R.drawable.pfp_unavailable),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(
                                        5.dp,
                                        if (winningUsername == playerWhite.username) colorResource(
                                            R.color.golden
                                        ) else colorResource(R.color.surface),
                                        CircleShape
                                    )
                            )
                            Text(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .width(150.dp),
                                text = playerWhite.username,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                                color = Color.White
                            )
                        }

                        Image(
                            modifier = Modifier.size(35.dp),
                            painter =
                                if (result == ResultType.DRAW) {
                                    painterResource(R.drawable.ic_draw)
                                } else {
                                    when (gameType) {
                                        GameType.RAPID -> painterResource(R.drawable.ic_rapid)
                                        GameType.BLITZ -> painterResource(R.drawable.ic_blitz)
                                        GameType.BULLET -> painterResource(R.drawable.ic_bullet)
                                    }
                                },
                            contentDescription = null
                        )
                        Column(
                            modifier = Modifier.size(150.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = playerBlack.photoUrl,
                                error = painterResource(R.drawable.pfp_unavailable),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(
                                        5.dp,
                                        if (winningUsername == playerBlack.username) colorResource(
                                            R.color.golden
                                        ) else colorResource(R.color.surface),
                                        CircleShape
                                    )
                            )
                            Text(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .width(150.dp),
                                text = playerBlack.username,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                        }
                    }

                    if (oldRating == null) {
                        Log.d(TAG, "initialRatings.value is null")
                        Toast.makeText(
                            context,
                            "Couldn't fetch your old rating",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (newRating != null) {
                            RatingDisplay(
                                gameType = gameType,
                                newRating = newRating,
                                oldRating = oldRating
                            )
                        }
                    }


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 3.dp),
                                shape = RoundedCornerShape(percent = 20),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(
                                        R.color.bg_pgn_btn
                                    )
                                ),
                                onClick = {
                                    pgnBtnClicked = true
                                    onExportPGNClicked {
                                        pgnBtnClicked = false
                                    }
                                }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        modifier = Modifier.padding(vertical = 15.dp),
                                        text = "Export PGN",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                                        color = textColor
                                    )
                                    if (pgnBtnClicked) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp).padding(start = 5.dp),
                                            color = Color.White,
                                            trackColor = colorResource(R.color.secondary),
                                            strokeWidth = 2.dp,
                                        )
                                    }
                                }
                            }
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 3.dp),
                                shape = RoundedCornerShape(percent = 20),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(
                                        R.color.secondary_dark
                                    )
                                ),
                                onClick = onNewGameClicked
                            ) {
                                Box{
                                    Image(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .alpha(0.1f)
                                            .align(Alignment.Center),
                                        painter = painterResource(
                                            when(gameType){
                                                GameType.BULLET -> R.drawable.ic_bullet
                                                GameType.BLITZ -> R.drawable.ic_blitz
                                                GameType.RAPID -> R.drawable.ic_rapid
                                            }
                                        ),
                                        contentDescription = null
                                    )
                                    Text(
                                        modifier = Modifier
                                            .padding(vertical = 15.dp)
                                            .align(Alignment.Center),
                                        text = "New $gameDuration min",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                                        color = textColor
                                    )
                                }
                            }
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            shape = RoundedCornerShape(percent = 20),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(
                                    R.color.secondary_dark
                                )
                            ),
                            onClick = onViewOpponentProfileClicked
                        ) {
                            Text(
                                modifier = Modifier.padding(vertical = 15.dp),
                                text =
                                    if (playerBlack.username == username)
                                        "View ${playerWhite.username}'s profile"
                                    else
                                        "View ${playerBlack.username}'s profile",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}

/*
@Preview(showSystemUi = true)
@Composable
private fun ResultDialogPreview() {
    ResultDialog(
        ResultType.WHITE,
        "CHECKMATE",
        1459,
        GameType.BULLET,
        10,
        User(
            "hey.idkrandom6",
            1500,
            1500,
            1500,
            ""
        ),
        User(
            "guranshsingh100",
            1500,
            1500,
            1500,
            ""
        ),
        "guranshsingh100",
        "hey.idkrandom6",
        RatingsRepository(DataStore<UserRatings>()),
        {},
        {},
        {},
        {},
        {})
}*/
