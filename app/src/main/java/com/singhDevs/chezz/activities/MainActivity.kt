package com.singhDevs.chezz.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.singhDevs.chezz.ChezzApplication
import com.singhDevs.chezz.screens.MainScreen
import com.singhDevs.chezz.ui.theme.ChezzTheme
import com.singhDevs.chezz.viewmodels.AuthViewModel

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by lazy {
        (application as ChezzApplication).authViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(TAG, "Inside MainActivity")

        authViewModel.authState.observe(this) { state ->
            when (state) {
                is AuthViewModel.AuthState.Unauthenticated -> {
                    Log.d(TAG, "AuthState: Unauthenticated, redirecting to SignInActivity...")
                    val intent = Intent(this, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                else -> {
                    Log.d(TAG, "AuthState: Authenticated, redirecting to HomeActivity...")
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }

        setContent {
            ChezzTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CircularProgressIndicator(
                        modifier = Modifier.padding(innerPadding),
                        color = Color.Blue,
                        strokeWidth = 4.dp
                    )
                }
            }
        }
    }
}