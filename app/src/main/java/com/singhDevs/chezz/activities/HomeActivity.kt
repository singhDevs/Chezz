package com.singhDevs.chezz.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.singhDevs.chezz.ChezzApplication
import com.singhDevs.chezz.auth.AuthManager
import com.singhDevs.chezz.network.User
import com.singhDevs.chezz.screens.MainScreen
import com.singhDevs.chezz.ui.theme.ChezzTheme
import com.singhDevs.chezz.utils.Constants
import com.singhDevs.chezz.viewmodels.AuthViewModel

private const val TAG = "HomeActivity"

class HomeActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager
    private lateinit var authViewModel: AuthViewModel
    private var token: String? = null
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if(application == null){
            Log.d(TAG, "Application is null, returning from HomeActivity...")
            finish()
        }

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

        setContent {
            ChezzTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        user = user!!,
                        onPlayGameClicked = {
                            val intent = Intent(this@HomeActivity, GameActivity::class.java)
                            intent.putExtra("token", token)
                            intent.putExtra("user", user)
                            intent.putExtra("gameTime", 5 * 60 * 1000)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}