package com.singhDevs.chezz.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.singhDevs.chezz.network.RetrofitClient
import com.singhDevs.chezz.network.User
import com.singhDevs.chezz.screens.MainScreen
import com.singhDevs.chezz.ui.theme.ChezzTheme
import com.singhDevs.chezz.utils.Constants
import com.singhDevs.chezz.viewmodels.AuthViewModel
import com.singhDevs.chezz.viewmodels.HomeActivityViewModel
import com.singhDevs.chezz.viewmodels.HomeViewModelFactory
import kotlinx.coroutines.CoroutineScope
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
    private var games: List<Game>? = null
    private var token: String? = null
    private var user: User? = null

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if(application == null){
            Log.d(TAG, "Application is null, returning from HomeActivity...")
            finish()
        }

        ratingsRepository = RatingsRepository(applicationContext)
        homeActivityViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(ratingsRepository)
        )[HomeActivityViewModel::class.java]

        authViewModel = (application as ChezzApplication).authViewModel
        authManager = (application as ChezzApplication).getAuthManager()
        token = authManager.getAuthToken()
        user = authManager.getUser()

        Log.d(TAG, "Authentication status: ${authViewModel.authState.value}")

        if(token == null || user == null){
            Log.e(TAG, "Token or User is null")
            Toast.makeText(this, "Couldn't fetch user details, try logging in again.", Toast.LENGTH_SHORT).show()
            finish()
        }

        Constants.user = user!!
        gameService = RetrofitClient.gameServiceInstance

        setContent {
            var games by remember { mutableStateOf<List<Game>?>(null) }
            CoroutineScope(Dispatchers.IO).launch {
                Log.d(TAG, "Fetching games...")
                val result = gameService.getGames("Bearer $token")
                if(result.isSuccessful){
                    games = result.body()?.games
                    Log.d(TAG, "Games fetched successfully: $games")
                }
                else{
                    Log.e(TAG, "Error fetching games: ${result.message()}")
                }
            }
            ChezzTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var showLoadingScreen by remember { mutableStateOf(false) }
                    if(!showLoadingScreen){
                        MainScreen(
                            modifier = Modifier.padding(innerPadding),
                            user = Constants.user,
                            viewModel = homeActivityViewModel,
                            onPlayGameClicked = { gameDuration, gameMode, gameType ->
                                val intent = Intent(this@HomeActivity, GameActivity::class.java)
                                intent.putExtra("token", token)
                                intent.putExtra("user", user)
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

                                    Log.d(TAG, "Authentication status: ${authViewModel.authState.value}")
                                    Toast.makeText(this@HomeActivity, "Signed out", Toast.LENGTH_SHORT).show()
                                    navigateToSignInActivity()
                                }
                            },
                            games = if(games == null) null else games!!
                        )
                    }
                    else{
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
                                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sign_out))
                                LottieAnimation(
                                    modifier = Modifier.size(120.dp),
                                    composition = composition,
                                    iterations = LottieConstants.IterateForever
                                )

                                Text(
                                    modifier = Modifier.padding(top = 8.dp),
                                    text = "Signing you out...",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = colorResource(R.color.text_light_square)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToSignInActivity(){
        val intent = Intent(this@HomeActivity, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}