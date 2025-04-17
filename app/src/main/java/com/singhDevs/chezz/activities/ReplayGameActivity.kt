package com.singhDevs.chezz.activities

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import com.singhDevs.chezz.R
import com.singhDevs.chezz.activities.ui.theme.ChezzTheme
import com.singhDevs.chezz.components.PlayerDisplayTab
import com.singhDevs.chezz.components.ReplayMovesListComposable
import com.singhDevs.chezz.models.Game
import com.singhDevs.chezz.models.GameMode
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.network.RetrofitClient
import com.singhDevs.chezz.screens.ReplayChessBoard
import com.singhDevs.chezz.utils.BasicUtils
import com.singhDevs.chezz.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ReplayGameActivity"

class ReplayGameActivity : ComponentActivity() {

    private var deviceBoard by mutableStateOf(Board())
    private var moves: List<Move> = emptyList()
    private lateinit var game: Game
    private lateinit var username: String

    private val fieldBackground = Color(0xFF2A2A2A)
    private val textColor = Color(0xFFE0E0E0)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val gameParcelable = intent.getParcelableExtra<Game>("game")
        if (gameParcelable == null) {
            Log.d(TAG, "gameParcelable is null!")
            finish()
        } else {
            game = gameParcelable
        }

        username = intent.getStringExtra("username").toString()

        val token = intent.getStringExtra("token")
        val user = if (game.whitePlayer.username == username) game.whitePlayer else game.blackPlayer
        val opponent = if (game.whitePlayer.username == username) game.blackPlayer else game.whitePlayer
        val color = if (user == game.whitePlayer) Side.WHITE else Side.BLACK


        Log.d(TAG, "Username: $username")
        Log.d(TAG, "User's color: $color")
        Log.d(TAG, "White's username: ${game.whitePlayer.username}")
        Log.d(TAG, "Black's username: ${game.blackPlayer.username}")

        val movesStringList = if(game.moves.isEmpty()) emptyList() else game.moves.split(" ")
        moves = if (movesStringList.isEmpty()) emptyList()
        else {
            movesStringList.mapIndexed { ind, it ->
                if (it[0] == 'O') {
                    if (it == "O-O") {
                        if (ind % 2 == 0) {
                            Move(
                                Constants.charToSquareMapping["e1"],
                                Constants.charToSquareMapping["g1"]
                            )
                        } else {
                            Move(
                                Constants.charToSquareMapping["e8"],
                                Constants.charToSquareMapping["g8"]
                            )
                        }
                    } else {
                        if (ind % 2 == 0) {
                            Move(
                                Constants.charToSquareMapping["e1"],
                                Constants.charToSquareMapping["c1"]
                            )
                        } else {
                            Move(
                                Constants.charToSquareMapping["e8"],
                                Constants.charToSquareMapping["c8"]
                            )
                        }
                    }
                }
                else if(it.length == 6){
                    Move(
                        Constants.charToSquareMapping[it[1].toString() + it[2].toString()],
                        Constants.charToSquareMapping[it[3].toString() + it[4].toString()],
                        if(ind % 2 == 0) Constants.whiteCharPieceToPiece[it[5]] else Constants.blackCharPieceToPiece[it[5]]
                    )
                }
                else {
                    Move(
                        Constants.charToSquareMapping[it[1].toString() + it[2].toString()],
                        Constants.charToSquareMapping[it[3].toString() + it[4].toString()]
                    )
                }
            }
        }

        setContent {
            ChezzTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = colorResource(R.color.game_background)
                            ),
                            title = {
                                Image(
                                    modifier = Modifier.size(150.dp),
                                    painter = painterResource(
                                        when (game.gameMode) {
                                            GameMode.CASUAL -> R.drawable.casual_banner
                                            GameMode.RATED -> R.drawable.rated_banner
                                        }
                                    ),
                                    contentDescription = null
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { finish() }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = null,
                                        tint = colorResource(R.color.primary_amber)
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        Log.d(TAG, "Fetching PGN data...")
                                        val gameService = RetrofitClient.gameServiceInstance
                                        CoroutineScope(Dispatchers.IO).launch {
                                            Log.d(
                                                TAG,
                                                "Hitting the /game/pgn endpoint, with token: $token, gameId: ${game.id}"
                                            )
                                            val response = gameService.getPGNData(
                                                token = "Bearer $token",
                                                gameId = game.id
                                            )

                                            if (!response.isSuccessful) {
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(
                                                        this@ReplayGameActivity,
                                                        "Error getting PGN data.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                return@launch
                                            } else {
                                                if (response.body() == null) {
                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(
                                                            this@ReplayGameActivity,
                                                            "Error getting PGN data.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    return@launch
                                                }
                                                val pgnData = response.body()!!.pgn
                                                Log.d(TAG, "PGN data: $pgnData")

                                                withContext(Dispatchers.Main) {
                                                    val sendIntent = Intent().apply {
                                                        action = Intent.ACTION_SEND
                                                        putExtra(Intent.EXTRA_TEXT, pgnData)
                                                        type = "text/plain"
                                                    }
                                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                                    startActivity(shareIntent)
                                                }
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Rounded.Share,
                                        contentDescription = null,
                                        tint = colorResource(R.color.primary_amber)
                                    )
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    var currIndex by remember { mutableIntStateOf(-1) }
                    val lazyListState: LazyListState = rememberLazyListState()
                    val coroutineScope = rememberCoroutineScope()

                    Box(
                        modifier = Modifier
                            .padding(bottom = innerPadding.calculateBottomPadding())
                            .fillMaxSize()
                            .background(colorResource(R.color.game_background))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            PlayerDisplayTab(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                username = opponent.username,
                                photoUrl = opponent.photoUrl ?: "",
                                gameModeIcon = when (game.gameMode) {
                                    GameMode.CASUAL -> null
                                    GameMode.RATED -> {
                                        when (game.gameType) {
                                            GameType.BULLET -> R.drawable.ic_bullet
                                            GameType.BLITZ -> R.drawable.ic_blitz
                                            GameType.RAPID -> R.drawable.ic_rapid
                                        }
                                    }
                                },
                                rating = when (game.gameMode) {
                                    GameMode.CASUAL -> null
                                    GameMode.RATED -> {
                                        if (color == Side.WHITE) game.blackPlayerRating
                                        else game.whitePlayerRating
                                    }
                                }
                            )

                            ReplayChessBoard(
                                context = this@ReplayGameActivity,
                                color = color,
                                deviceBoard = deviceBoard,
                                movesStringList = movesStringList,
                                currIndex = currIndex
                            )

                            PlayerDisplayTab(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                username = user.username,
                                photoUrl = user.photoUrl ?: "",
                                gameModeIcon = when (game.gameMode) {
                                    GameMode.CASUAL -> null
                                    GameMode.RATED -> {
                                        when (game.gameType) {
                                            GameType.BULLET -> R.drawable.ic_bullet
                                            GameType.BLITZ -> R.drawable.ic_blitz
                                            GameType.RAPID -> R.drawable.ic_rapid
                                        }
                                    }
                                },
                                rating = when (game.gameMode) {
                                    GameMode.CASUAL -> null
                                    GameMode.RATED -> {
                                        if (color == Side.WHITE) game.whitePlayerRating
                                        else game.blackPlayerRating
                                    }
                                }
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .padding(5.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(colorResource(R.color.see_more_btn)),
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (moves.isNotEmpty()) {
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 15.dp, top = 10.dp)
                                            .fillMaxWidth(),
                                        text = "Moves",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        textAlign = TextAlign.Start
                                    )

                                    ReplayMovesListComposable(
                                        movesStringList,
                                        lazyListState,
                                        currIndex
                                    ) {
                                        Log.d(TAG, "Move index selected: $it")
                                        if(it < currIndex){
                                            if (moves.isNotEmpty()) {
                                                while (currIndex != it) {
                                                    deviceBoard.undoMove()
                                                    currIndex -= 1
                                                }
                                                coroutineScope.launch {
                                                    lazyListState.animateScrollToItem(index = currIndex)
                                                }
                                            }
                                        }
                                        else if (it > currIndex){
                                            if (moves.isNotEmpty()) {
                                                while (currIndex != it) {
                                                    deviceBoard.doMove(moves[currIndex + 1])
                                                    currIndex += 1
                                                }
                                                coroutineScope.launch {
                                                    lazyListState.animateScrollToItem(index = currIndex)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = "No moves were made in this game!",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                    )
                                }
                            }

                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 20.dp, top = 10.dp, start = 5.dp, end = 5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){
                                IconButton(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(fieldBackground),
                                    onClick = {
                                        if (moves.isNotEmpty()) {
                                            if(currIndex > 0){
                                                while (currIndex > 0) {
                                                    deviceBoard.undoMove()
                                                    currIndex -= 1
                                                }
                                                coroutineScope.launch {
                                                    lazyListState.animateScrollToItem(index = 0)
                                                }
                                                deviceBoard.undoMove()
                                                currIndex -= 1
                                            }
                                        }
                                        Log.d(TAG, "currIndex after ic_first: $currIndex")
                                        Log.d(TAG, "deviceBoard after ic_next: $deviceBoard")
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(18.dp),
                                        painter = painterResource(R.drawable.ic_first),
                                        contentDescription = null,
                                        tint = BasicUtils.getColor(
                                            this@ReplayGameActivity,
                                            R.color.primary_amber
                                        )
                                    )
                                }
                                Text(
                                    modifier = Modifier.padding(top = 5.dp),
                                    text = "to the Start",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = textColor
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){
                                IconButton(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(fieldBackground),
                                    onClick = {
                                        if (moves.isNotEmpty() && currIndex == 0) {
                                            deviceBoard.undoMove()
                                            currIndex -= 1
                                            coroutineScope.launch {
                                                lazyListState.animateScrollToItem(index = 0)
                                            }
                                        }
                                        if (moves.isNotEmpty() && currIndex - 1 >= 0) {
                                            deviceBoard.undoMove()
                                            currIndex -= 1
                                            coroutineScope.launch {
                                                lazyListState.animateScrollToItem(index = currIndex)
                                            }
                                        }
                                        Log.d(TAG, "currIndex after ic_previous: $currIndex")
                                        Log.d(TAG, "deviceBoard after ic_next: $deviceBoard")
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(18.dp),
                                        painter = painterResource(R.drawable.ic_previous),
                                        contentDescription = null,
                                        tint = BasicUtils.getColor(
                                            this@ReplayGameActivity,
                                            R.color.primary_amber
                                        )
                                    )
                                }
                                Text(
                                    modifier = Modifier.padding(top = 5.dp),
                                    text = "Previous",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = textColor
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){
                                IconButton(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(fieldBackground),
                                    onClick = {
                                        if (moves.isNotEmpty() && currIndex + 1 != moves.size) {
                                            deviceBoard.doMove(moves[currIndex + 1])
                                            currIndex += 1
                                        }
                                        coroutineScope.launch {
                                            lazyListState.animateScrollToItem(index = currIndex)
                                        }
                                        Log.d(TAG, "currIndex after ic_next: $currIndex")
                                        Log.d(TAG, "deviceBoard after ic_next: $deviceBoard")
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(18.dp),
                                        painter = painterResource(R.drawable.ic_next),
                                        contentDescription = null,
                                        tint = BasicUtils.getColor(
                                            this@ReplayGameActivity,
                                            R.color.primary_amber
                                        )
                                    )
                                }
                                Text(
                                    modifier = Modifier.padding(top = 5.dp),
                                    text = "Next",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = textColor
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){
                                IconButton(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(fieldBackground),
                                    onClick = {
                                        if (moves.isNotEmpty()) {
                                            while (currIndex + 1 != moves.size) {
                                                deviceBoard.doMove(moves[currIndex + 1])
                                                currIndex += 1
                                            }
                                            coroutineScope.launch {
                                                lazyListState.animateScrollToItem(index = currIndex)
                                            }
                                        }
                                        Log.d(TAG, "currIndex after ic_last: $currIndex")
                                        Log.d(TAG, "deviceBoard after ic_next: $deviceBoard")
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(18.dp),
                                        painter = painterResource(R.drawable.ic_last),
                                        contentDescription = null,
                                        tint = BasicUtils.getColor(
                                            this@ReplayGameActivity,
                                            R.color.primary_amber
                                        )
                                    )
                                }
                                Text(
                                    modifier = Modifier.padding(top = 5.dp),
                                    text = "to the Last",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}