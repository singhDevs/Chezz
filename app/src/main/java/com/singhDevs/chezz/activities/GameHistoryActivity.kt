package com.singhDevs.chezz.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singhDevs.chezz.R
import com.singhDevs.chezz.activities.ui.theme.ChezzTheme
import com.singhDevs.chezz.components.GameHistoryComposable
import com.singhDevs.chezz.models.Game
import com.singhDevs.chezz.network.User

private const val TAG = "GameHistoryActivity"

class GameHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val user = intent.getParcelableExtra<User>("user")
        val gamesSerializable = intent.getSerializableExtra("gamesList")
        if(user == null || gamesSerializable == null){
            Log.d(TAG, "User or games is null!")
            finish()
        }
        
        val games = gamesSerializable as List<Game>

        setContent {
            ChezzTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RecentGames(user = user!!, games = games, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun RecentGames(games: List<Game>, user: User, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.secondary_dark))
    ){
        Text(
            text = "Recent Games",
            fontSize = 30.sp,
            fontWeight = FontWeight.Light,
            color = Color.White,
            modifier = Modifier.padding(start = 20.dp, top = 50.dp)
        )

        LazyColumn(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(games) {
                GameHistoryComposable(
                    it,
                    user.username
                )
            }
        }
    }
}