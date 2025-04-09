package com.singhDevs.chezz.screens

import android.content.Intent
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.singhDevs.chezz.R
import com.singhDevs.chezz.activities.GameHistoryActivity
import com.singhDevs.chezz.components.AnimatedDropdownArrow
import com.singhDevs.chezz.components.GameHistoryComposable
import com.singhDevs.chezz.components.GameModeComposable
import com.singhDevs.chezz.components.HomeProfileComposable
import com.singhDevs.chezz.components.PlayerProfileDialog
import com.singhDevs.chezz.components.RatingHistoryChart
import com.singhDevs.chezz.components.TimeDurationTypesComposable
import com.singhDevs.chezz.models.Game
import com.singhDevs.chezz.models.GameMode
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.models.Ratings
import com.singhDevs.chezz.network.User
import com.singhDevs.chezz.viewmodels.HomeActivityViewModel
import com.singhDevs.chezz.viewmodels.HomeViewModelFactory
import java.io.Serializable

private const val TAG = "MainScreen"

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    user: User,
    viewModel: HomeActivityViewModel,
    onPlayGameClicked: (gameDuration: Int, gameMode: GameMode, gameType: GameType) -> Unit,
    onSigningOut: () -> Unit,
    games: List<Game>? = null
) {
    val ratings by viewModel.ratings.collectAsState()

    val context = LocalContext.current
    var btnExpanded by remember { mutableStateOf(false) }
    var shouldShowRating by remember { mutableStateOf(true) }
    var gameDuration by remember { mutableIntStateOf(5 * 60 * 1000) }
    var gameMode by remember { mutableStateOf(GameMode.RATED) }
    var gameType by remember { mutableStateOf(GameType.BLITZ) }
    var shouldShowMoreGamesTitle by remember { mutableStateOf(false) }

    var showProfileDialog by remember { mutableStateOf(false) }
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(colorResource(R.color.background))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                .background(colorResource(R.color.background)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(35.dp),
                painter = painterResource(R.drawable.ic_blitz),
                contentDescription = null
            )
            Image(
                modifier = Modifier.size(110.dp),
                painter = painterResource(R.drawable.ic_chezz_logo),
                contentDescription = null
            )
            IconButton(
                onClick = { showProfileDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A2A2A))
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(percent = 20))
                        .clickable {
                            showProfileDialog = true
                        },
                    model = user.photoUrl,
                    error = painterResource(R.drawable.pfp_unavailable),
                    contentDescription = null
                )
            }
        }

        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Log.d(TAG, "Sending shouldShowRating: $shouldShowRating")
            HomeProfileComposable(
                user = user,
                ratings = ratings,
                ratingToShow = gameType,
                shouldShowRating = shouldShowRating
            )

            Button(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 22.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        when (gameMode) {
                            GameMode.RATED -> colorResource(R.color.bg_rated)
                            GameMode.CASUAL -> colorResource(R.color.bg_casual)
                        },
                    contentColor = when (gameMode) {
                        GameMode.RATED -> colorResource(R.color.bg_rated_txt)
                        GameMode.CASUAL -> colorResource(R.color.bg_casual_txt)
                    }
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 10.dp,
                    pressedElevation = 4.dp
                ),
                onClick = { onPlayGameClicked(gameDuration, gameMode, gameType) }
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 22.dp, horizontal = 5.dp)
                        .animateContentSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 22.dp, horizontal = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .weight(1f),
                            text = "Play Game",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier
                                .clickable {
                                    Log.d(TAG, "Dropdown clicked")
                                    if (btnExpanded) {
                                        btnExpanded = false
                                    } else {
                                        btnExpanded = true
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(35.dp)
                                    .padding(5.dp),
                                painter = painterResource(
                                    when (gameType) {
                                        GameType.BULLET -> R.drawable.ic_bullet
                                        GameType.BLITZ -> R.drawable.ic_blitz
                                        GameType.RAPID -> R.drawable.ic_rapid
                                    }
                                ),
                                contentDescription = null
                            )
                            Text(
                                text =
                                    when (gameDuration) {
                                        1 * 60 * 1000 -> "1 min"
                                        3 * 60 * 1000 -> "3 min"
                                        5 * 60 * 1000 -> "5 min"
                                        10 * 60 * 1000 -> "10 min"
                                        30 * 60 * 1000 -> "30 min"
                                        else -> "5 min"
                                    },
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Light
                            )
                            AnimatedDropdownArrow(expanded = btnExpanded)
                        }
                    }

                    /*if (btnExpanded) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 20.dp),
                            thickness = 0.5.dp
                        )
                    }*/

                    var question by remember { mutableIntStateOf(1) }
                    if (btnExpanded) {
                        Crossfade(
                            targetState = question,
                            label = "dropDown"
                        ) { screen ->
                            when (screen) {
                                1 ->
                                    GameModeComposable { mode ->
                                        gameMode = mode
                                        shouldShowRating = (mode == GameMode.RATED)
                                        question = 2
                                    }

                                2 -> TimeDurationTypesComposable(gameMode = gameMode) { duration, type ->
                                    gameDuration = duration
                                    gameType = type
                                    question = 1
                                    btnExpanded = false
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.75f)
                    .then(if (games.isNullOrEmpty()) Modifier.fillMaxHeight(0.4f) else Modifier.wrapContentHeight())
                    .padding(5.dp)
                    .clip(RoundedCornerShape(percent = 5))
                    .background(colorResource(R.color.secondary_dark))
            ) {
                if (games != null) {
                    Log.d(TAG, "Games: $games")
                    if (games.size > 5) shouldShowMoreGamesTitle = true

                    if (games.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(
                                    R.raw.no_games
                                )
                            )
                            LottieAnimation(
                                modifier = Modifier.size(120.dp),
                                composition = composition,
                                iterations = LottieConstants.IterateForever
                            )
                            Text(
                                text = "Games you play show here...",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Light,
                                color = Color.White
                            )
                        }
                    } else {
                        Text(
                            text = "Recent Games",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Light,
                            color = Color.White,
                            modifier = Modifier.padding(start = 20.dp, top = 20.dp)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(games.take(5)) {
                                GameHistoryComposable(
                                    it,
                                    user.username
                                )
                            }

                            if (shouldShowMoreGamesTitle) {
                                item {
                                    Button(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(5.dp),
                                        shape = RoundedCornerShape(percent = 28),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = colorResource(
                                                R.color.see_more_btn
                                            )
                                        ),
                                        onClick = {
                                            val intent = Intent(context, GameHistoryActivity::class.java)
                                            intent.putExtra("user", user)
                                            intent.putExtra("gamesList", games as Serializable)
                                            context.startActivity(intent)
                                        }
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(vertical = 2.5.dp),
                                            text = "see more Games...",
                                            fontSize = 20.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Light
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Games is null")
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(
                                R.raw.no_games
                            )
                        )
                        LottieAnimation(
                            modifier = Modifier.size(120.dp),
                            composition = composition,
                            iterations = LottieConstants.IterateForever
                        )
                        Text(
                            text = "Games you play show here...",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Light,
                            color = Color.White
                        )
                    }
                }
            }

            // Ratings History
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121212))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Your Performance",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
                )

                RatingHistoryChart(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showProfileDialog) {
        PlayerProfileDialog(
            context = context,
            user = user,
            games = games,
            onDismiss = { showProfileDialog = false },
            onSignOut = {
                Log.d(TAG, "onSignOut clicked, signing out...")
                showProfileDialog = false
                onSigningOut()
                /*
                Do sign out cleanup, show a loading screen showing you are signing out
                1. Clear EncryptedPrefs
                        Remove the session token
                        Remove any authentication-related data
                        Clear user credentials (if stored)

                2. Clear Proto DataStore
                    Remove all user profile information
                    Clear any user preferences or settings that aren't meant to persist across logins

                3. Memory Cleanup
                    Clear any in-memory caches related to the user
                    Reset any singleton objects or repositories that might hold user data
                Ensure viewModels are cleared or recreated
                 */
            }
        )
    }
}
//}
/*

@Preview(showSystemUi = true)
@Composable
private fun MainScreenPreview() {
    MainScreen(
        onPlayGameClicked = {_, _, _ ->},
        user = User(
            "",
            "pluta@gmail.com",
            "pluta",
            "https://firebasestorage.googleapis.com/v0/b/musix-a6206.appspot.com/o/banner%2Fabout_coldplay.jpg?alt=media&token=35ce63d9-ddb6-4115-af5e-4cd38f65cc99",
            Ratings()
        ),
        games = listOf(
            Game(
                "",
                whitePlayer = com.singhDevs.chezz.models.User(
                    "pluta",
                    1500,
                    1500,
                    1500,
                    "https://firebasestorage.googleapis.com/v0/b/musix-a6206.appspot.com/o/banner%2Fabout_coldplay.jpg?alt=media&token=35ce63d9-ddb6-4115-af5e-4cd38f65cc99"
                ),
                whitePlayerId = "",
                blackPlayerId = "",
                blackPlayer = com.singhDevs.chezz.models.User(
                    "hehe",
                    1500,
                    1500,
                    1500,
                    "https://firebasestorage.googleapis.com/v0/b/musix-a6206.appspot.com/o/banner%2Fabout_coldplay.jpg?alt=media&token=35ce63d9-ddb6-4115-af5e-4cd38f65cc99"
                ),
                moves = "",
                gameDuration = 600,
                gameType = GameType.RAPID,
                result = "w",
                winningUser = "pluta",
                termination = "checkmate"
            ),
            Game(
                "",
                whitePlayer = com.singhDevs.chezz.models.User(
                    "pluta",
                    1500,
                    1500,
                    1500,
                    "https://firebasestorage.googleapis.com/v0/b/musix-a6206.appspot.com/o/banner%2Fabout_coldplay.jpg?alt=media&token=35ce63d9-ddb6-4115-af5e-4cd38f65cc99"
                ),
                whitePlayerId = "",
                blackPlayerId = "",
                blackPlayer = com.singhDevs.chezz.models.User(
                    "hehe",
                    1500,
                    1500,
                    1500,
                    "https://firebasestorage.googleapis.com/v0/b/musix-a6206.appspot.com/o/banner%2Fabout_coldplay.jpg?alt=media&token=35ce63d9-ddb6-4115-af5e-4cd38f65cc99"
                ),
                moves = "",
                gameDuration = 600,
                gameType = GameType.RAPID,
                result = "b",
                winningUser = "hehe",
                termination = "checkmate"
            ),
            Game(
                "",
                whitePlayer = com.singhDevs.chezz.models.User(
                    "pluta",
                    1500,
                    1500,
                    1500,
                    "https://firebasestorage.googleapis.com/v0/b/musix-a6206.appspot.com/o/banner%2Fabout_coldplay.jpg?alt=media&token=35ce63d9-ddb6-4115-af5e-4cd38f65cc99"
                ),
                whitePlayerId = "",
                blackPlayerId = "",
                blackPlayer = com.singhDevs.chezz.models.User(
                    "hehe",
                    1500,
                    1500,
                    1500,
                    "https://firebasestorage.googleapis.com/v0/b/musix-a6206.appspot.com/o/banner%2Fabout_coldplay.jpg?alt=media&token=35ce63d9-ddb6-4115-af5e-4cd38f65cc99"
                ),
                moves = "",
                gameDuration = 600,
                gameType = GameType.RAPID,
                result = "d",
                winningUser = "~",
                termination = "draw by agreement"
            )
        )
    )
}*/
