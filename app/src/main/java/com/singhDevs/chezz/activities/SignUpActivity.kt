package com.singhDevs.chezz.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.singhDevs.chezz.ChezzApplication
import com.singhDevs.chezz.R
import com.singhDevs.chezz.activities.ui.theme.ChezzTheme
import com.singhDevs.chezz.auth.AuthManager
import com.singhDevs.chezz.data.RatingsRepository
import com.singhDevs.chezz.network.AuthService
import com.singhDevs.chezz.network.EmailSignInRequest
import com.singhDevs.chezz.network.RetrofitClient
import com.singhDevs.chezz.screens.ChezzAppTheme
import com.singhDevs.chezz.screens.SignUpScreen
import com.singhDevs.chezz.utils.Constants
import com.singhDevs.chezz.viewmodels.AuthViewModel
import com.singhDevs.chezz.viewmodels.SignInViewModel
import com.singhDevs.chezz.viewmodels.SignInViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "SignUpActivity"

class SignUpActivity : ComponentActivity() {
    private lateinit var authService: AuthService
    private lateinit var authViewModel: AuthViewModel
    private val authManager: AuthManager by lazy {
        (application as ChezzApplication).getAuthManager()
    }
    private lateinit var ratingsRepository: RatingsRepository
    private lateinit var signInViewModel: SignInViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ratingsRepository = RatingsRepository(applicationContext)
        signInViewModel = ViewModelProvider(
            this,
            SignInViewModelFactory(ratingsRepository)
        )[SignInViewModel::class.java]

        authService = RetrofitClient.authServiceInstance
        authViewModel = (application as ChezzApplication).authViewModel

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ChezzTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = colorResource(R.color.background)
                            ),
                            title = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Create Account",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 5.dp)
                                    )

                                    Text(
                                        text = "Start your chezz journey",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                                        color = colorResource(R.color.text_secondary)
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        finish()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = ChezzAppTheme.PrimaryAmber
                                    )
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    SignUpScreen(
                        modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                        context = this,
                        onNavigateToLogin = { finish() },
                        onSignUpClick = { email, password ->
                            lifecycleScope.launch {
                                try {
                                    val response = authService.signUpWithEmail(
                                        EmailSignInRequest(
                                            email,
                                            password
                                        )
                                    )
                                    if (response.isSuccessful) {
                                        val authResponse = response.body()
                                        if (authResponse == null) {
                                            Log.e(
                                                TAG,
                                                "Authentication failed: Response body is null"
                                            )
                                            showError("Authentication failed: Please try again later.")
                                            return@launch
                                        }

                                        // Saving credentials in Encrypted Shared Preferences
                                        authManager.saveUserData(
                                            authResponse.token,
                                            authResponse.user
                                        )
                                        Constants.user = authResponse.user

                                        // Saving ratings in Proto DataStore
                                        Log.d(TAG, "Saving user ratings...")
                                        signInViewModel.saveAllRatings(authResponse.user.ratings)

                                        CoroutineScope(Dispatchers.IO).launch {
                                            authViewModel.checkAuthState()
                                            Log.d(
                                                TAG,
                                                "Auth State updated. Current auth state: ${authViewModel.authState.value}"
                                            )
                                        }
                                        Log.d(
                                            TAG,
                                            "Authentication successful, Received token: ${authResponse.token}"
                                        )
                                        Toast.makeText(
                                            this@SignUpActivity,
                                            "Signed in!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val intent =
                                            Intent(this@SignUpActivity, HomeActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    } else {
                                        showError("User not found. Please sign up first.")
                                        return@launch
                                    }
                                } catch (e: Exception) {
                                    showError("Network error: Error signing you in")
                                    Log.d(TAG, "Network error: ${e.message}")
                                    return@launch
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}