package com.singhDevs.chezz.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singhDevs.chezz.R
import com.singhDevs.chezz.activities.ui.theme.ChezzTheme
import com.singhDevs.chezz.components.GameHistoryComposable
import com.singhDevs.chezz.models.Game
import com.singhDevs.chezz.network.User
import com.singhDevs.chezz.screens.ChezzAppTheme

private const val TAG = "GameHistoryActivity"
private val surfaceColor = Color(0xFF1E1E1E)

class GameHistoryActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val user = intent.getParcelableExtra<User>("user")
        val token = intent.getStringExtra("token")
        val gamesSerializable = intent.getSerializableExtra("gamesList")
        if (user == null || gamesSerializable == null || token == null) {
            Log.d(TAG, "User or games or token is null!")
            finish()
        }

        val games = gamesSerializable as List<Game>

        setContent {
            ChezzTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = surfaceColor),
                            title = {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Recent Games",
                                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
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
                    RecentGames(
                        user = user!!,
                        token = token!!,
                        games = games,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun RecentGames(games: List<Game>, token: String, user: User, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(surfaceColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(games) {
                GameHistoryComposable(
                    LocalContext.current,
                    it,
                    token,
                    user.username
                )
            }
        }
    }
}