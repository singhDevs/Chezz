package com.singhDevs.chezz.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.singhDevs.chezz.screens.ProfileScreen
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.singhDevs.chezz.R
import com.singhDevs.chezz.activities.ui.theme.ChezzTheme
import com.singhDevs.chezz.network.RetrofitClient
import com.singhDevs.chezz.network.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "ProfileActivity"

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userIdIntent = intent.getStringExtra("userId")
        val userIdDL = intent?.data?.getQueryParameter("userId") ?: -1
        val userService = RetrofitClient.userServiceInstance
        val userId: String = if (userIdIntent.isNullOrEmpty()) userIdDL.toString()
        else userIdIntent.toString()

        enableEdgeToEdge()
        setContent {
            ChezzTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var isDataLoaded by remember { mutableStateOf(false) }
                    var user by remember { mutableStateOf<User?>(null) }

                    LaunchedEffect(Unit) {
                        Log.d(TAG, "Fetching user profile for userId: $userId...")
                        val response = userService.getUserProfile(userId)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                if (response.body() == null) {
                                    Toast.makeText(
                                        this@ProfileActivity,
                                        "Error loading profile",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isDataLoaded = true
                                    return@withContext
                                }
                                user = response.body()!!.user
                                isDataLoaded = true
                            } else {
                                Toast.makeText(
                                    this@ProfileActivity,
                                    "Error loading profile",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    if (isDataLoaded) {
                        ProfileScreen(
                            context = this,
                            user = user!!,
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        var dots by remember { mutableStateOf(".") }
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(500)
                                dots = when (dots.length) {
                                    1 -> ".."
                                    2 -> "..."
                                    else -> "."
                                }
                            }
                        }

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
                                        R.raw.chess_loading
                                    )
                                )
                                LottieAnimation(
                                    modifier = Modifier.size(120.dp),
                                    composition = composition,
                                    iterations = LottieConstants.IterateForever
                                )

                                Text(
                                    modifier = Modifier.padding(top = 5.dp),
                                    text = "Loading Player profile$dots",
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
}