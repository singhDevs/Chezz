package com.singhDevs.chezz.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.singhDevs.chezz.ChezzApplication
import com.singhDevs.chezz.R
import com.singhDevs.chezz.auth.AuthManager
import com.singhDevs.chezz.components.LoadingDialog
import com.singhDevs.chezz.data.RatingsRepository
import com.singhDevs.chezz.models.Game
import com.singhDevs.chezz.network.GameService
import com.singhDevs.chezz.network.RatingHistoryItem
import com.singhDevs.chezz.network.RetrofitClient
import com.singhDevs.chezz.network.User
import com.singhDevs.chezz.screens.MainScreen
import com.singhDevs.chezz.ui.theme.ChezzTheme
import com.singhDevs.chezz.utils.Constants
import com.singhDevs.chezz.viewmodels.AuthViewModel
import com.singhDevs.chezz.viewmodels.HomeActivityViewModel
import com.singhDevs.chezz.viewmodels.HomeViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "HomeActivity"

class HomeActivity : ComponentActivity() {
    private lateinit var ratingsRepository: RatingsRepository
    private lateinit var homeActivityViewModel: HomeActivityViewModel

    private lateinit var authManager: AuthManager
    private lateinit var authViewModel: AuthViewModel
    private lateinit var gameService: GameService
    private var token: String? = null
    private var user: User? = null

    private var isConnected by mutableStateOf(true)

    private var games by mutableStateOf<List<Game>?>(null)
    private var bulletRatingHistory by mutableStateOf<List<RatingHistoryItem>?>(null)
    private var blitzRatingHistory by mutableStateOf<List<RatingHistoryItem>?>(null)
    private var rapidRatingHistory by mutableStateOf<List<RatingHistoryItem>?>(null)

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (application == null) {
            Log.d(TAG, "Application is null, returning from HomeActivity...")
            finish()
        }

        ratingsRepository = RatingsRepository(applicationContext)
        homeActivityViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(ratingsRepository)
        )[HomeActivityViewModel::class.java]

        lifecycleScope.launch {
            homeActivityViewModel.ratingsFlow.collect {
                homeActivityViewModel.setRatings(it)
            }
        }

        authViewModel = (application as ChezzApplication).authViewModel
        authManager = (application as ChezzApplication).getAuthManager()
        token = authManager.getAuthToken()
        user = authManager.getUser()

        Log.d(TAG, "Authentication status: ${authViewModel.authState.value}")

        if (token == null || user == null) {
            Log.e(TAG, "Token or User is null")
            Toast.makeText(
                this,
                "Couldn't fetch user details, try logging in again.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        Constants.user = user!!
        gameService = RetrofitClient.gameServiceInstance
        fetchGamesAndRatingsHistory {
            isConnected = false
        }

        setContent {
            ChezzTheme {
                var showProfileDialog by remember { mutableStateOf(false) }
                var showLoadingScreen by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        if (!showLoadingScreen && isConnected) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                                color = colorResource(id = R.color.background)
                            ) {
                                CenterAlignedTopAppBar(
                                    modifier = Modifier.padding(
                                        bottom = 5.dp,
                                        start = 5.dp,
                                        end = 5.dp
                                    ),
                                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                        containerColor = Color.Transparent
                                    ),
                                    title = {
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            Image(
                                                modifier = Modifier
                                                    .size(180.dp)
                                                    .align(Alignment.Center),
                                                painter = painterResource(R.drawable.chezz_banner),
                                                contentDescription = null
                                            )
                                            IconButton(
                                                onClick = { showProfileDialog = true },
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .align(Alignment.CenterEnd)
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
                                                    model = user!!.photoUrl,
                                                    error = painterResource(R.drawable.pfp_unavailable),
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (!isConnected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colorResource(R.color.game_background)),
                            contentAlignment = Alignment.Center
                        ) {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(
                                    R.raw.no_network
                                )
                            )
                            Column(
                                modifier = Modifier.padding(15.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                LottieAnimation(
                                    modifier = Modifier.size(150.dp),
                                    composition = composition,
                                    iterations = LottieConstants.IterateForever
                                )
                                Text(
                                    text = "Can't connect to the network",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = colorResource(R.color.text_light_square)
                                )
                            }
                        }
                    }
                    if (!showLoadingScreen && isConnected) {
                        MainScreen(
                            modifier = Modifier.padding(innerPadding),
                            user = Constants.user,
                            token = token!!,
                            viewModel = homeActivityViewModel,
                            showProfileDialog = showProfileDialog,
                            dismissProfileDialog = { showProfileDialog = false },
                            onPlayGameClicked = { gameDuration, gameMode, gameType ->
                                val intent = Intent(this@HomeActivity, GameActivity::class.java)
                                intent.putExtra("token", token)
                                intent.putExtra("user", user)
                                intent.putExtra("ratings", homeActivityViewModel.ratings)
                                intent.putExtra("duration", gameDuration)
                                intent.putExtra("mode", gameMode)
                                intent.putExtra("type", gameType)
                                startActivity(intent)
                            },
                            onSigningOut = {
                                showLoadingScreen = true
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        authViewModel.signOut(applicationContext)
                                    }

                                    Log.d(
                                        TAG,
                                        "Authentication status: ${authViewModel.authState.value}"
                                    )
                                    Toast.makeText(
                                        this@HomeActivity,
                                        "Signed out",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navigateToSignInActivity()
                                }
                            },
                            gamesList = if (games == null) null else games!!,
                            bulletRatingHistory = if (bulletRatingHistory == null) null else bulletRatingHistory!!,
                            blitzRatingHistory = if (blitzRatingHistory == null) null else blitzRatingHistory!!,
                            rapidRatingHistory = if (rapidRatingHistory == null) null else rapidRatingHistory!!
                        )
                    } else if (!showLoadingScreen && isConnected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colorResource(R.color.game_background)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.padding(15.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val composition by rememberLottieComposition(
                                    LottieCompositionSpec.RawRes(
                                        R.raw.sign_out
                                    )
                                )
                                LottieAnimation(
                                    modifier = Modifier.size(120.dp),
                                    composition = composition,
                                    iterations = LottieConstants.IterateForever
                                )

                                Text(
                                    modifier = Modifier.padding(top = 8.dp),
                                    text = "Signing you out...",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = colorResource(R.color.text_light_square)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun fetchGamesAndRatingsHistory(onFailure: () -> Unit) {
        lifecycleScope.launch {
            Log.d(TAG, "Fetching games & rating history in onResume...")
            try {
                val result = gameService.getGamesAndRatingsHistory("Bearer $token")
                if (result.isSuccessful) {
                    games = result.body()?.games
                    bulletRatingHistory = result.body()?.bulletRatingHistory
                    blitzRatingHistory = result.body()?.blitzRatingHistory
                    rapidRatingHistory = result.body()?.rapidRatingHistory

                    Log.d(TAG, "Games fetched successfully: $games")
                } else {
                    Log.e(TAG, "Couldn't fetch games & ratings history: ${result.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching games & ratings history: ${e.message}")
                onFailure()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchGamesAndRatingsHistory {
            isConnected = false
        }
    }

    private fun navigateToSignInActivity() {
        val intent = Intent(this@HomeActivity, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}