package com.singhDevs.chezz.activities

import android.content.Intent
import android.media.Rating
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import com.google.gson.Gson
import com.singhDevs.chezz.ChezzApplication
import com.singhDevs.chezz.R
import com.singhDevs.chezz.UserRatingsOuterClass.UserRatings
import com.singhDevs.chezz.components.DrawDialog
import com.singhDevs.chezz.components.LoadingDialog
import com.singhDevs.chezz.components.MovesListComposable
import com.singhDevs.chezz.components.PlayerDisplayTab
import com.singhDevs.chezz.components.PopupDialog
import com.singhDevs.chezz.components.TimerComposable
import com.singhDevs.chezz.data.RatingsRepository
import com.singhDevs.chezz.models.GameMode
import com.singhDevs.chezz.models.GameOverResponse
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.models.Message
import com.singhDevs.chezz.models.MessageTypes
import com.singhDevs.chezz.models.ResultType
import com.singhDevs.chezz.models.chessboard.Board
import com.singhDevs.chezz.network.GameService
import com.singhDevs.chezz.network.JoinGameRequest
import com.singhDevs.chezz.network.RetrofitClient
import com.singhDevs.chezz.network.User
import com.singhDevs.chezz.screens.ChessBoard
import com.singhDevs.chezz.ui.theme.ChezzTheme
import com.singhDevs.chezz.utils.BasicUtils
import com.singhDevs.chezz.utils.Constants
import com.singhDevs.chezz.viewmodels.AuthViewModel
import com.singhDevs.chezz.viewmodels.ChessBoardViewModel
import com.singhDevs.chezz.viewmodels.ChessViewModelFactory
import com.singhDevs.chezz.viewmodels.HomeActivityViewModel
import com.singhDevs.chezz.viewmodels.HomeViewModelFactory
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

    private lateinit var ratingsRepository: RatingsRepository
    private lateinit var chessBoardViewModel: ChessBoardViewModel

    private lateinit var authViewModel: AuthViewModel

    private lateinit var gameService: GameService

    private var whiteTime by mutableLongStateOf(5 * 60 * 1000)
    private var blackTime by mutableLongStateOf(5 * 60 * 1000)

    private var deviceBoard by mutableStateOf(com.github.bhlangonijr.chesslib.Board())
    private var turn by mutableStateOf(Side.WHITE)
    private var board by mutableStateOf(Board())
    private var result: ResultType? by mutableStateOf(null)
    private var gameOverResponse by mutableStateOf<GameOverResponse?>(null)
    private var onlineUsers: Int? by mutableStateOf(null)
    private var cause: String? by mutableStateOf(null)
    private var color by mutableStateOf('a')
    private var isGameReady by mutableStateOf(false)
    private var isTimerRunning by mutableStateOf(false)
    private var showResignDialog by mutableStateOf(false)
    private var showDrawDialog by mutableStateOf(false)
    private var opponentColor by mutableStateOf('a')
    private var opponent by mutableStateOf<com.singhDevs.chezz.models.User?>(null)
    private var gameDuration by mutableIntStateOf(0)
    private var gameType by mutableStateOf(GameType.BLITZ)
    private var gameMode by mutableStateOf(GameMode.RATED)
    private var legalMoves by mutableStateOf<MutableList<Move>?>(null)
    private val movesList = mutableStateListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ratingsRepository = RatingsRepository(applicationContext)
        chessBoardViewModel = ViewModelProvider(
            this,
            ChessViewModelFactory(ratingsRepository)
        )[ChessBoardViewModel::class.java]

        authViewModel = (application as ChezzApplication).authViewModel

        val token = intent.getStringExtra("token")
        val user = intent.getParcelableExtra<User>("user")
        val duration = intent.getIntExtra("duration", 5 * 60 * 1000)
        gameType = intent.getSerializableExtra("type") as GameType
        gameMode = intent.getSerializableExtra("mode") as GameMode

        lifecycleScope.launch {
            chessBoardViewModel.initialize(duration.toLong())
        }

        if (token == null || user == null) {
            Log.e(TAG, "Token or User is null!")
            finish()
        }

        gameService = RetrofitClient.gameServiceInstance
        lifecycleScope.launch {
            joinGame(token, user, gameType, gameMode, duration)
        }

        setContent {
            ChezzTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (!isGameReady) {
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
                                if (opponent != null) {
                                    PlayerDisplayTab(
                                        username = opponent!!.username,
                                        photoUrl = opponent!!.photoUrl ?: "",
                                        gameModeIcon = when (gameMode) {
                                            GameMode.CASUAL -> null
                                            GameMode.RATED -> {
                                                when (gameType) {
                                                    GameType.BULLET -> R.drawable.ic_bullet
                                                    GameType.BLITZ -> R.drawable.ic_blitz
                                                    GameType.RAPID -> R.drawable.ic_rapid
                                                }
                                            }
                                        },
                                        rating = when (gameMode) {
                                            GameMode.CASUAL -> null
                                            GameMode.RATED -> {
                                                when (gameType) {
                                                    GameType.BULLET -> opponent!!.bulletRating
                                                    GameType.BLITZ -> opponent!!.blitzRating
                                                    GameType.RAPID -> opponent!!.rapidRating
                                                }
                                            }
                                        }
                                    )
                                } else {
                                    PlayerDisplayTab(
                                        username = "Opponent",
                                        photoUrl = ""
                                    )
                                }
                                TimerComposable(
                                    time = BasicUtils.millisToString(if (color == 'b') whiteTime else if (color == 'w') blackTime else 0),
                                    isTimerRunning = isTimerRunning,
                                    onTimerStopped = {
                                        isTimerRunning = false
                                    }
                                )
                            }
                            ChessBoard(
                                modifier = Modifier.padding(innerPadding),
                                user = Constants.user,
                                opponent = opponent!!,
                                color = Constants.colorToSideMapping[color]!!,
                                gameDuration = gameDuration,
                                gameType = gameType,
                                gameMode = gameMode,
                                context = this@GameActivity,
                                deviceBoard = deviceBoard,
                                turn = turn,
                                changeTurn = {
                                    Log.d(TAG, "Previous turn: $turn")
                                    turn = if (turn == Side.WHITE) Side.BLACK
                                    else Side.WHITE
                                    Log.d(TAG, "Turn changed to: $turn")
                                },
                                legalBoardMoves = legalMoves,
                                result = result,
                                cause = cause,
                                gameOverResponse = gameOverResponse,
                                viewModel = chessBoardViewModel,
                                onMoveMade = { move ->
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
                                    movesList.add(move)
                                    Log.d(
                                        TAG,
                                        "Size: ${movesList.size}\tLatest move added: ${if (movesList.isNotEmpty()) movesList[movesList.size - 1] else "movesList is empty!"}"
                                    )
                                },
                                onNewGameClicked = {
                                    isGameReady = false
                                    lifecycleScope.launch {
                                        this@GameActivity.apply {
                                            cause = null
                                            result = null
                                            gameOverResponse = null
                                            deviceBoard = com.github.bhlangonijr.chesslib.Board()
                                            board = Board()
                                            legalMoves = null
                                            movesList.clear()
                                        }
                                        Constants.webSocketClient.webSocket?.close(
                                            1000,
                                            "Current game is finished, finding a new game."
                                        )
                                        joinGame(token, user, gameType, gameMode, duration)
                                    }
                                },
                                onExportPGNClicked = { toggleProgressIndicator ->
                                    Log.d(TAG, "Fetching PGN data...")
                                    val gameService = RetrofitClient.gameServiceInstance
                                    CoroutineScope(Dispatchers.IO).launch {
                                        Log.d(
                                            TAG,
                                            "Hitting the /game/pgn endpoint, with token: $token, gameId: ${gameOverResponse!!.id}"
                                        )
                                        val response = gameService.getPGNData(
                                            token = "Bearer $token",
                                            gameId = gameOverResponse!!.id
                                        )

                                        if (!response.isSuccessful) {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(
                                                    this@GameActivity,
                                                    "Error getting PGN data.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            toggleProgressIndicator()
                                            return@launch
                                        } else {
                                            if (response.body() == null) {
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(
                                                        this@GameActivity,
                                                        "Error getting PGN data.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                toggleProgressIndicator()
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
                                                val shareIntent =
                                                    Intent.createChooser(sendIntent, null)
                                                startActivity(shareIntent)
                                            }
                                            toggleProgressIndicator()
                                        }
                                    }
                                }
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!showDrawDialog) {
                                    PlayerDisplayTab(
                                        username = Constants.user.username,
                                        photoUrl = Constants.user.photoUrl ?: ""
                                    )
                                } else {
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
                                TimerComposable(
                                    time = BasicUtils.millisToString(if (color == 'w') whiteTime else if (color == 'b') blackTime else 0),
                                    isTimerRunning = isTimerRunning,
                                    onTimerStopped = {
                                        isTimerRunning = false
                                    }
                                )
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
                                MovesListComposable(movesList, lazyListState)
                            } else {
                                Spacer(
                                    Modifier
                                        .height(12.dp)
                                        .fillMaxWidth()
                                )
                            }

                            if(isTimerRunning){
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

    private suspend fun joinGame(
        token: String?,
        user: User?,
        gameType: GameType,
        gameMode: GameMode,
        duration: Int
    ) {
        Log.d(TAG, "Hitting the /game/join endpoint, with token: $token, userId: ${user!!.id}")
        val response = gameService.joinGame(
            "Bearer $token",
            JoinGameRequest(user.id, duration, gameMode, gameType)
        )
        if (!response.isSuccessful) {
            Log.e(TAG, "Failed to join game: ${response.message()}")
            withContext(Dispatchers.Main) {
                if (response.message() == "Unauthorized") {
                    Toast.makeText(
                        this@GameActivity,
                        "Session expired. Login again to continue",
                        Toast.LENGTH_SHORT
                    ).show()
                    authViewModel.getAuthManager().clearCredentials()
                    val intent = Intent(this@GameActivity, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this@GameActivity, "Error joining the game.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            return
        }
        if (response.body() == null) {
            Log.e(TAG, "Response body is null!")
            withContext(Dispatchers.Main) {
                Toast.makeText(this@GameActivity, "Error joining the game.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
            return
        }
        Log.d(TAG, "/game/join endpoint response, Response body: ${response.body()}")
        Constants.webSocketClient.start(
            response.body()!!.wsURL,
            token!!,
            duration.toString(),
            gameMode,
            gameType
        )
    }

    override fun onInfoReceived(onlineUsers: Int) {
        this.onlineUsers = onlineUsers
    }

    override fun onGameStart(
        color: Char,
        opponent: com.singhDevs.chezz.models.User,
        duration: Int,
        gameType: GameType
    ) {
        this.color = color
        this.isGameReady = true
        this.opponentColor = if (color == 'w') 'b' else 'w'
        this.opponent = opponent
        this.gameDuration = duration / (60 * 1000)
        this.gameType = gameType
    }

    override fun onMoveMade(
        board: Board,
        move: com.singhDevs.chezz.models.Move,
        piece: Char,
        whiteTime: Long,
        blackTime: Long
    ) {
        Log.d(TAG, "onMoveMade: $board")

        val moveString = if (move.kingSideCastle) "O-O";
        else if (move.queenSideCastle) "O-O-O";
        else BasicUtils.generateMoveString(move.from, move.to, piece, opponentColor)

        movesList.add(moveString)
        Log.d(
            TAG,
            "Size: ${movesList.size}\tLatest move added: ${if (movesList.isNotEmpty()) movesList[movesList.size - 1] else "movesList is empty!"}"
        )

        chessBoardViewModel.apply {
            onServerTimeSync(whiteTime, blackTime)
            if (color == 'w') {
                Log.d(TAG, "OPPONENT move made, starting WHITE...")
                turn = Side.WHITE
                _whiteTimer.start()
                _blackTimer.pause()
            } else if (color == 'b') {
                Log.d(TAG, "OPPONENT move made, starting BLACK...")
                turn = Side.BLACK
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

        Log.d(TAG, "onMoveMade, printing deviceBoard:-\n$deviceBoard")
        Log.d(TAG, "now its turn of: ${deviceBoard.sideToMove}")
        Log.d(TAG, "Now printing Legal available moves:-")
        this.legalMoves?.forEach { legalMove ->
            Log.d(TAG, legalMove.toString())
        }
    }

    override fun onGameOver(gameOverResponse: GameOverResponse) {
        Log.d(TAG, "GAME OVER - result: $result")
        chessBoardViewModel.stopTimers()
        this.result = gameOverResponse.result
        this.cause = gameOverResponse.cause
        this.gameOverResponse = gameOverResponse

        val move = gameOverResponse.move

        if ((result == ResultType.WHITE && color == 'w') || (result == ResultType.BLACK && color == 'b') || result == ResultType.DRAW) {
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