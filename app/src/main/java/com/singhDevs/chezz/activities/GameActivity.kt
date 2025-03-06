package com.singhDevs.chezz.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import com.google.gson.Gson
import com.singhDevs.chezz.R
import com.singhDevs.chezz.components.DrawDialog
import com.singhDevs.chezz.components.LoadingDialog
import com.singhDevs.chezz.components.PlayerDisplayTab
import com.singhDevs.chezz.components.PopupDialog
import com.singhDevs.chezz.components.TimerComposable
import com.singhDevs.chezz.models.Message
import com.singhDevs.chezz.models.MessageTypes
import com.singhDevs.chezz.models.chessboard.Board
import com.singhDevs.chezz.network.AuthService
import com.singhDevs.chezz.network.JoinGameRequest
import com.singhDevs.chezz.network.RetrofitClient
import com.singhDevs.chezz.network.User
import com.singhDevs.chezz.screens.ChessBoard
import com.singhDevs.chezz.ui.theme.ChezzTheme
import com.singhDevs.chezz.utils.BasicUtils
import com.singhDevs.chezz.utils.Constants
import com.singhDevs.chezz.viewmodels.ChessBoardViewModel
import com.singhDevs.chezz.websocket.MessageActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "WebSocketClient"

class GameActivity : ComponentActivity(), MessageActions {
    init {
        Constants.initClient(this)
    }

    private var gameTime: Long = 5 * 60 * 1000
    private lateinit var authService: AuthService
    private val chessBoardViewModel = ChessBoardViewModel(5 * 60 * 1000)

    private var whiteTime by mutableLongStateOf(gameTime)
    private var blackTime by mutableLongStateOf(gameTime)

    private var deviceBoard by mutableStateOf(com.github.bhlangonijr.chesslib.Board())
    private var board by mutableStateOf(Board())
    private var result: Char? by mutableStateOf(null)
    private var onlineUsers: Int? by mutableStateOf(null)
    private var cause: String? by mutableStateOf(null)
    private var color by mutableStateOf('a')
    private var showResignDialog by mutableStateOf(false)
    private var showDrawDialog by mutableStateOf(false)
    private var opponentColor by mutableStateOf('a')
    private var opponent by mutableStateOf("")
    private var legalMoves by mutableStateOf<MutableList<Move>?>(null)
    private val movesList = mutableStateListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val token = intent.getStringExtra("token")
        val user = intent.getParcelableExtra<User>("user")
        this.gameTime = intent.getLongExtra("gameTime", 5 * 60 * 1000)

        if (token == null || user == null) {
            Log.e(TAG, "Token or User is null!")
            finish()
        }

        authService = RetrofitClient.instance
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Hitting the /game/join endpoint, with token: $token, userId: ${user!!.id}")
            val response = authService.joinGame("Bearer $token", JoinGameRequest(user.id))
            if (!response.isSuccessful) {
                Log.e(TAG, "Failed to join game: ${response.errorBody()}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GameActivity, "Error joining the game.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
                return@launch
            }
            if (response.body() == null) {
                Log.e(TAG, "Response body is null!")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GameActivity, "Error joining the game.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
                return@launch
            }
            Log.d(TAG, "/game/join endpoint response, Response body: ${response.body()}")
            Constants.webSocketClient.start(response.body()!!.wsURL, token!!)
        }

        setContent {
            ChezzTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (color == 'a') {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colorResource(R.color.game_background))
                        ) {
                            LoadingDialog(
                                modifier = Modifier.align(Alignment.Center),
                                onlineUsers = onlineUsers ?: 0
                            )
                            IconButton(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .navigationBarsPadding()
                                    .padding(vertical = 30.dp)
                                    .size(58.dp),
                                onClick = {
                                    Constants.webSocketClient.webSocket?.close(
                                        1000,
                                        "Game cancelled"
                                    )
                                    finish()
                                }
                            ) {
                                Icon(
                                    modifier = Modifier.size(58.dp),
                                    painter = painterResource(R.drawable.ic_cancel),
                                    contentDescription = null,
                                    tint = colorResource(R.color.cancel_btn_color)
                                )
                            }
                        }
                    } else {
                        LaunchedEffect(Unit) {
                            //Observers observing current times of White and Black sides
                            chessBoardViewModel.apply {
                                whiteCurrentTime.observe(this@GameActivity) { whiteTime = it }
                                blackCurrentTime.observe(this@GameActivity) { blackTime = it }
                                _whiteTimer.start()
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colorResource(R.color.game_background)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PlayerDisplayTab(
                                    username = opponent,
                                    photoUrl = "https://firebasestorage.googleapis.com/v0/b/musix-a6206.appspot.com/o/banner%2Fabout_karan_aujla.jpg?alt=media&token=4b6dc927-c080-4c6d-ba14-9d0a9a1b02df"
                                )
                                TimerComposable(time = BasicUtils.millisToString(if (color == 'b') whiteTime else if (color == 'w') blackTime else 0))
                            }
                            ChessBoard(
                                messageActions = this@GameActivity,
                                color = Constants.colorToSideMapping[color]!!,
                                username = Constants.user.username,
                                opponent = opponent,
                                context = this@GameActivity,
                                modifier = Modifier.padding(innerPadding),
                                board = board,
                                deviceBoard = deviceBoard,
                                legalBoardMoves = legalMoves,
                                result = result,
                                cause = cause,
                                onMoveMade = { from, to, piece ->
                                    chessBoardViewModel.apply {
                                        if (color == 'w') {
                                            Log.d(TAG, "OUR move made, starting BLACK...")
                                            _whiteTimer.pause()
                                            _blackTimer.start()
                                        } else if (color == 'b') {
                                            Log.d(TAG, "OUR move made, starting WHITE...")
                                            _blackTimer.pause()
                                            _whiteTimer.start()
                                        }
                                    }
                                    movesList.add(
                                        BasicUtils.generateMoveString(
                                            from,
                                            to,
                                            piece,
                                            color
                                        )
                                    )
                                    Log.d(
                                        TAG,
                                        "Size: ${movesList.size}\tLatest move added: ${if (movesList.isNotEmpty()) movesList[movesList.size - 1] else "movesList is empty!"}"
                                    )
                                }
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if(!showDrawDialog){
                                    PlayerDisplayTab(
                                        username = Constants.user.username,
                                        photoUrl = "https://firebasestorage.googleapis.com/v0/b/musix-a6206.appspot.com/o/banner%2Fabout_coldplay.jpg?alt=media&token=35ce63d9-ddb6-4115-af5e-4cd38f65cc99"
                                    )
                                }
                                else{
                                    DrawDialog(
                                        Modifier,
                                        onDrawAccepted = {
                                            val msg = Gson().toJson(
                                                Message(type = MessageTypes.DRAW.value)
                                            )
                                            Constants.webSocketClient.webSocket?.send(msg)
                                            showDrawDialog = false
                                        },
                                        onDrawRejected = {
                                            showDrawDialog = false
                                        }
                                    )
                                }
                                TimerComposable(time = BasicUtils.millisToString(if (color == 'w') whiteTime else if (color == 'b') blackTime else 0))
                            }

                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                    .fillMaxWidth(),
                                text = "REPLAY",
                                color = colorResource(R.color.replay_text),
                                textAlign = TextAlign.Start,
                                fontFamily = FontFamily.Monospace
                            )

                            if (movesList.isNotEmpty()) {
                                val lazyListState: LazyListState = rememberLazyListState()
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    state = lazyListState
                                ) {
                                    itemsIndexed(movesList) { index, move ->
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                modifier = Modifier.padding(5.dp),
                                                text = "${index + 1}.",
                                                fontSize = 20.sp,
                                                color = Color.Gray,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Image(
                                                modifier = Modifier
                                                    .padding(end = 3.dp)
                                                    .size(25.dp),
                                                painter = painterResource(
                                                    if (move[0] == 'w')
                                                        Constants.whitePieceToSymbol[move[1]]!!
                                                    else
                                                        Constants.blackPieceToSymbol[move[1]]!!
                                                ),
                                                contentDescription = null
                                            )
                                            Text(
                                                modifier = Modifier.padding(start = 5.dp),
                                                text = move.slice(2..<move.length),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colorResource(R.color.replay_text)
                                            )
                                            ScrollToEnd(movesList, lazyListState)
                                        }
                                    }
                                }
                            } else {
                                Spacer(
                                    Modifier
                                        .height(12.dp)
                                        .fillMaxWidth()
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 5.dp, vertical = 5.dp)
                                        .drawBehind {
                                            val borderSize = 3.dp.toPx()
                                            drawLine(
                                                color = Color.LightGray,
                                                start = Offset(0f, size.height),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = borderSize
                                            )
                                        },
                                    shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        colorResource(R.color.game_buttons_color)
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = colorResource(
                                            R.color.game_background
                                        )
                                    ),
                                    onClick = {
                                        Log.d(TAG, "Resignation button clicked.")
                                        showResignDialog = true
                                    }) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            modifier = Modifier.size(30.dp),
                                            painter = painterResource(R.drawable.ic_resign),
                                            contentDescription = null
                                        )
                                        Text(
                                            modifier = Modifier.padding(start = 15.dp),
                                            text = "RESIGN",
                                            fontSize = 18.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                                Button(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 5.dp, vertical = 5.dp)
                                        .drawBehind {
                                            val borderSize = 3.dp.toPx()
                                            drawLine(
                                                color = Color.LightGray,
                                                start = Offset(0f, size.height),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = borderSize
                                            )
                                        },
                                    shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        colorResource(R.color.game_buttons_color)
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = colorResource(
                                            R.color.draw_button_color
                                        )
                                    ),
                                    onClick = {
                                        Log.d(TAG, "Draw button clicked.")
                                        showDrawDialog = true
                                    }) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            modifier = Modifier.size(30.dp),
                                            painter = painterResource(R.drawable.ic_draw),
                                            contentDescription = null
                                        )
                                        Text(
                                            modifier = Modifier.padding(start = 15.dp),
                                            text = "DRAW",
                                            fontSize = 18.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (showResignDialog) {
                        PopupDialog(
                            message = "Are you sure you want to resign?",
                            onClickOutside = { showResignDialog = false },
                            negativeButtonAction = { showResignDialog = false },
                            positiveButtonAction = {
                                val msg = Gson().toJson(
                                    Message(type = "resign")
                                )
                                Constants.webSocketClient.webSocket?.send(msg)
                                showResignDialog = false
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ScrollToEnd(movesList: List<String>, lazyListState: LazyListState) {
        Log.d(TAG, "ScrollToEnd called")
        LaunchedEffect(movesList.size) {
            if (movesList.isNotEmpty()) {
                lazyListState.animateScrollToItem(movesList.size - 1)
            }
        }
    }

    override fun onInfoReceived(onlineUsers: Int) {
        this.onlineUsers = onlineUsers
    }

    override fun onGameStart(color: Char, opponent: String) {
        this.color = color
        this.opponentColor = if (color == 'w') 'b' else 'w'
        this.opponent = opponent
    }

    override fun onMoveMade(
        board: Board,
        move: com.singhDevs.chezz.models.Move,
        piece: Char,
        result: Char?,
        cause: String?,
        whiteTime: Long,
        blackTime: Long
    ) {
        Log.d(TAG, "onMoveMade: $board")

        movesList.add(BasicUtils.generateMoveString(move.from, move.to, piece, opponentColor))
        Log.d(
            TAG,
            "Size: ${movesList.size}\tLatest move added: ${if (movesList.isNotEmpty()) movesList[movesList.size - 1] else "movesList is empty!"}"
        )

        chessBoardViewModel.apply {
            onServerTimeSync(whiteTime, blackTime)
            if (color == 'w') {
                Log.d(TAG, "OPPONENT move made, starting WHITE...")
                _whiteTimer.start()
                _blackTimer.pause()
            } else if (color == 'b') {
                Log.d(TAG, "OPPONENT move made, starting BLACK...")
                _blackTimer.start()
                _whiteTimer.pause()
            }
        }

        deviceBoard.doMove(
            Move(
                Constants.charToSquareMapping[move.from],
                Constants.charToSquareMapping[move.to]
            )
        )
        this.board = board
        this.legalMoves = deviceBoard.legalMoves()
        this.result = result
        this.cause = cause

        Log.d(TAG, "onMoveMade, printing deviceBoard:-\n$deviceBoard")
        Log.d(TAG, "now its turn of: ${deviceBoard.sideToMove}")
        Log.d(TAG, "Now printing Legal available moves:-")
        this.legalMoves?.forEach { legalMove ->
            Log.d(TAG, legalMove.toString())
        }
    }

    override fun onGameOver(result: Char?, cause: String?, move: com.singhDevs.chezz.models.Move) {
        this.result = result
        this.cause = cause
        chessBoardViewModel.stopTimers()

        if ((result == 'w' && color == 'w') || (result == 'b' && color == 'b') || result == 'd' || result == 'r') {
            return
        } else {
            movesList.add(
                BasicUtils.generateMoveString(
                    move.from,
                    move.to,
                    move.piece[0],
                    opponentColor
                )
            )
            Log.d(
                TAG,
                "Size: ${movesList.size}\tLatest move added: ${if (movesList.isNotEmpty()) movesList[movesList.size - 1] else "movesList is empty!"}"
            )
        }
    }

    override fun onDrawRequested() {
        showDrawDialog = true
    }

    override fun onDestroy() {
        super.onDestroy()
        Constants.webSocketClient.webSocket?.close(1000, "Activity destroyed")
    }
}