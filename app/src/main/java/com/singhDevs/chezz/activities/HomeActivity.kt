package com.singhDevs.chezz.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.singhDevs.chezz.ChezzApplication
import com.singhDevs.chezz.auth.AuthManager
import com.singhDevs.chezz.di.RatingsRepository
import com.singhDevs.chezz.models.Game
import com.singhDevs.chezz.network.AuthService
import com.singhDevs.chezz.network.GameService
import com.singhDevs.chezz.network.RetrofitClient
import com.singhDevs.chezz.network.User
import com.singhDevs.chezz.screens.MainScreen
import com.singhDevs.chezz.ui.theme.ChezzTheme
import com.singhDevs.chezz.utils.Constants
import com.singhDevs.chezz.viewmodels.AuthViewModel
import com.singhDevs.chezz.viewmodels.HomeActivityViewModel
import com.singhDevs.chezz.viewmodels.HomeViewModelFactory
import com.singhDevs.chezz.viewmodels.SignInViewModel
import com.singhDevs.chezz.viewmodels.SignInViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                        games = if(games == null) null else games!!
                    )
                }
            }
        }
    }
}