package com.singhDevs.chezz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.singhDevs.chezz.models.chessboard.Board
import com.singhDevs.chezz.screens.MainScreen
import com.singhDevs.chezz.ui.theme.ChessTheme

private const val TAG = "WebSocketClient"

class MainActivity : ComponentActivity() {

    var board by mutableStateOf<Board?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ChessTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(context = this, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}