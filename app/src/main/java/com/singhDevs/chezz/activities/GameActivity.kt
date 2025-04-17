package com.singhDevs.chezz.activities

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import com.google.gson.Gson
import com.singhDevs.chezz.ChezzApplication
import com.singhDevs.chezz.R
import com.singhDevs.chezz.components.ConfettiComposable
import com.singhDevs.chezz.components.DrawDialog
import com.singhDevs.chezz.components.LoadingDialog
import com.singhDevs.chezz.components.MovesListComposable
import com.singhDevs.chezz.components.PlayerDisplayTab
import com.singhDevs.chezz.components.PromotionOptionsComposable
import com.singhDevs.chezz.components.ResignDialog
import com.singhDevs.chezz.components.TimerComposable
import com.singhDevs.chezz.data.RatingsRepository
import com.singhDevs.chezz.models.GameMode
import com.singhDevs.chezz.models.GameOverResponse
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.models.Message
import com.singhDevs.chezz.models.MessageTypes
import com.singhDevs.chezz.models.Ratings
import com.singhDevs.chezz.models.ResultType
import com.singhDevs.chezz.models.UserWithoutCreds
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
import com.singhDevs.chezz.websocket.MessageActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

private const val TAG = "GameActivity"

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

    private var deviceBoard by mutableStateOf(Board())
    private var legalMoves by mutableStateOf<MutableList<Move>?>(null)

    private var gameOverResponse by mutableStateOf<GameOverResponse?>(null)
    private var onlineUsers: Int? by mutableStateOf(null)
    private var color by mutableStateOf('a')
    private var isGameReady by mutableStateOf(false)
    private var isGameOver by mutableStateOf(false)
    private var showResignDialog by mutableStateOf(false)
    private var showDrawDialog by mutableStateOf(false)
    private var opponentColor by mutableStateOf('a')

    private var opponent by mutableStateOf<UserWithoutCreds?>(null)
    private var user by mutableStateOf<User?>(null)

    private var gameDuration by mutableIntStateOf(0)
    private var gameType by mutableStateOf(GameType.BLITZ)
    private var gameMode by mutableStateOf(GameMode.RATED)
    private var showPromotionOptions by mutableStateOf(false)
    private val movesList = mutableStateListOf<String>()

    private var newRatings by mutableStateOf<Ratings?>(null)
    private var oldRatings by mutableStateOf<Ratings?>(null)

    private val textColor = Color(0xFFE0E0E0)
    private val fieldBackground = Color(0xFF2A2A2A)


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
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
        user = intent.getParcelableExtra("user")
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
                Scaffold(
                    topBar = {
                        if (isGameReady) {
                            CenterAlignedTopAppBar(
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = colorResource(R.color.game_background)
                                ),
                                title = {
                                    Image(
                                        modifier = Modifier.size(150.dp),
                                        painter = painterResource(
                                            when (gameMode) {
                                                GameMode.CASUAL -> R.drawable.casual_banner
                                                GameMode.RATED -> R.drawable.rated_banner
                                            }
                                        ),
                                        contentDescription = null
                                    )
                                },
                                navigationIcon = {
                                    if(isGameOver){
                                        IconButton(
                                            onClick = { finish() }
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                                contentDescription = null,
                                                tint = colorResource(R.color.primary_amber)
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    newRatings = chessBoardViewModel.userRatings.collectAsState().value
                    if (oldRatings == null) oldRatings = newRatings

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

                        Box(
                            modifier = Modifier
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
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!showPromotionOptions) {
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
                                                            GameType.BULLET -> opponent!!.ratings?.bulletRating
                                                            GameType.BLITZ -> opponent!!.ratings?.blitzRating
                                                            GameType.RAPID -> opponent!!.ratings?.rapidRating
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
                                    } else {
                                        PromotionOptionsComposable(
                                            color,
                                            onPromotionPieceSelected = { piece ->
                                                Log.d(
                                                    TAG,
                                                    "setting promotedPiece in viewModel to: $piece"
                                                )
                                                chessBoardViewModel.setPromotedPiece(piece)
                                                showPromotionOptions = false
                                            }
                                        )
                                    }
                                    TimerComposable(
                                        time = BasicUtils.millisToString(if (color == 'b') whiteTime else if (color == 'w') blackTime else 0),
                                        isTimerPaused =
                                            if (!isGameOver) {
                                                if (color == 'b')
                                                    deviceBoard.sideToMove != Side.WHITE  // If it's WHITE's turn, then TIMER is NOT PAUSED!
                                                else
                                                    deviceBoard.sideToMove != Side.BLACK
                                            } else true,
                                        isGameOver = isGameOver
                                    )
                                }
                                ChessBoard(
                                    context = this@GameActivity,
                                    user = Constants.user,
                                    opponent = opponent!!,
                                    color = Constants.colorToSideMapping[color]!!,
                                    gameDuration = gameDuration,
                                    gameType = gameType,
                                    gameMode = gameMode,
                                    deviceBoard = deviceBoard,
                                    legalBoardMoves = legalMoves,
                                    onPromotionSquareTapped = {
                                        showPromotionOptions = true
                                    },
                                    showPromotionOptions = showPromotionOptions,
                                    onPromotionsOptionsDismiss = {
                                        chessBoardViewModel.setPromotedPiece(Piece.NONE)
                                        showPromotionOptions = false
                                    },
                                    gameOverResponse = gameOverResponse,
                                    oldRatings = oldRatings,
                                    viewModel = chessBoardViewModel,
                                    onMoveMade = { moveStr ->
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
                                        movesList.add(moveStr)
                                        Log.d(
                                            TAG,
                                            "Size: ${movesList.size}\tLatest move added: ${if (movesList.isNotEmpty()) movesList[movesList.size - 1] else "movesList is empty!"}"
                                        )
                                    },
                                    onNewGameClicked = {
                                        isGameReady = false
                                        lifecycleScope.launch {
                                            this@GameActivity.apply {
                                                if (gameOverResponse != null && gameOverResponse!!.updatedRatings != null) {
                                                    chessBoardViewModel.setUserRatings(
                                                        gameOverResponse!!.updatedRatings!!.apply {
                                                            Ratings(
                                                                bulletRating,
                                                                blitzRating,
                                                                rapidRating
                                                            )
                                                        }
                                                    )
                                                }

                                                chessBoardViewModel.initialize(duration.toLong())
                                                opponent = null
                                                gameOverResponse = null
                                                isGameReady = false
                                                isGameOver = false
                                                deviceBoard = Board()
                                                legalMoves = null
                                                movesList.clear()

                                                showDrawDialog = false
                                                showResignDialog = false
                                                showPromotionOptions = false
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
                                            photoUrl = Constants.user.photoUrl ?: "",
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
                                                        GameType.BULLET -> newRatings?.bulletRating
                                                        GameType.BLITZ -> newRatings?.blitzRating
                                                        GameType.RAPID -> newRatings?.rapidRating
                                                    }
                                                }
                                            }
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
                                        isTimerPaused =
                                            if (!isGameOver) {
                                                if (color == 'b')
                                                    deviceBoard.sideToMove != Side.BLACK  // If it's BLACK's turn, then TIMER is NOT PAUSED!
                                                else
                                                    deviceBoard.sideToMove != Side.WHITE
                                            } else true,
                                        isGameOver = isGameOver
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(95.dp)
                                        .padding(5.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(colorResource(R.color.see_more_btn)),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (movesList.isNotEmpty()) {
                                        Text(
                                            modifier = Modifier
                                                .padding(start = 15.dp, top = 10.dp)
                                                .fillMaxWidth(),
                                            text = "Moves",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White,
                                            textAlign = TextAlign.Start
                                        )

                                        val lazyListState: LazyListState = rememberLazyListState()
                                        MovesListComposable(movesList, lazyListState)
                                    } else {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = "Game moves are shown here...",
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White,
                                        )
                                    }
                                }
                            }

                            if (!isGameOver) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 25.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        IconButton(
                                            onClick = {
                                                Log.d(TAG, "Resignation button clicked.")
                                                showResignDialog = true
                                            },
                                            modifier = Modifier
                                                .size(52.dp)
                                                .clip(CircleShape)
                                                .background(fieldBackground)
                                        ) {
                                            Icon(
                                                modifier = Modifier.padding(15.dp),
                                                painter = painterResource(R.drawable.ic_resign),
                                                contentDescription = null,
                                                tint = textColor
                                            )
                                        }
                                        Text(
                                            modifier = Modifier.padding(top = 5.dp),
                                            text = "Resign",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = textColor
                                        )
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        IconButton(
                                            onClick = {
                                                Log.d(TAG, "Draw button clicked.")
                                                showDrawDialog = true
                                            },
                                            modifier = Modifier
                                                .size(52.dp)
                                                .clip(CircleShape)
                                                .background(fieldBackground)
                                        ) {
                                            Icon(
                                                modifier = Modifier.padding(15.dp),
                                                painter = painterResource(R.drawable.ic_draw),
                                                contentDescription = null,
                                                tint = textColor
                                            )
                                        }
                                        Text(
                                            modifier = Modifier.padding(top = 5.dp),
                                            text = "Draw",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = textColor
                                        )
                                    }
                                }

                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 25.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (gameOverResponse?.winningUser == user?.username)
                                                chessBoardViewModel.festive()
                                            else
                                                chessBoardViewModel.rain(
                                                    Party(
                                                        speed = 10f,
                                                        maxSpeed = 30f,
                                                        damping = 0.9f,
                                                        angle = Angle.BOTTOM,
                                                        spread = 30,
                                                        colors = listOf(0x0000FF),
                                                        emitter = Emitter(
                                                            duration = 5,
                                                            TimeUnit.SECONDS
                                                        ).perSecond(100),
                                                        position = Position.Relative(0.0, 0.0)
                                                            .between(Position.Relative(1.0, 0.0)),
                                                        size = listOf(Size.MEDIUM),
                                                        shapes = listOf(Shape.Circle)
                                                    )
                                                )
                                        },
                                        modifier = Modifier
                                            .size(52.dp)
                                            .background(
                                                color = fieldBackground,
                                                shape = CircleShape
                                            )
                                            .clip(CircleShape)
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                id =
                                                    if (gameOverResponse?.winningUser == user?.username) R.drawable.ic_party_popper
                                                    else R.drawable.ic_rain_cloud
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.padding(18.dp),
                                            tint = Color.Unspecified
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (showResignDialog) {
                        ResignDialog(
                            message = "Are you sure you want to resign?",
                            onDismiss = { showResignDialog = false },
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

                    ConfettiComposable(viewModel = chessBoardViewModel)
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
                    finish()
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
        opponent: UserWithoutCreds,
        duration: Int,
        gameType: GameType
    ) {
        this.color = color
        this.isGameReady = true
        this.opponentColor = if (color == 'w') 'b' else 'w'
        this.opponent = opponent
        this.gameDuration = duration / (60 * 1000)
        this.gameType = gameType
        this.legalMoves = this.deviceBoard.legalMoves()
    }

    override fun onMoveMade(
        move: com.singhDevs.chezz.models.Move,
        piece: Char,
        whiteTime: Long,
        blackTime: Long
    ) {
        val moveString = if (move.kingSideCastle) "O-O"
        else if (move.queenSideCastle) "O-O-O"
        else if (move.promotion != null) BasicUtils.generateMoveString(
            move.from,
            move.to,
            "$piece",
            opponentColor,
            move.promotion
        )
        else BasicUtils.generateMoveString(move.from, move.to, "$piece", opponentColor)

        movesList.add(moveString)
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

        if (move.promotion != null) {
            deviceBoard.doMove(
                Move(
                    Constants.charToSquareMapping[move.from],
                    Constants.charToSquareMapping[move.to],
                    if (opponentColor == 'w')
                        Constants.whiteCharPieceToPiece[move.promotion.toCharArray()[0]]
                    else
                        Constants.blackCharPieceToPiece[move.promotion.toCharArray()[0]]
                )
            )
        } else {
            deviceBoard.doMove(
                Move(
                    Constants.charToSquareMapping[move.from],
                    Constants.charToSquareMapping[move.to]
                )
            )
        }
        this.legalMoves = deviceBoard.legalMoves()

        Log.d(TAG, "onMoveMade, printing deviceBoard:-\n$deviceBoard")
        Log.d(TAG, "now its turn of: ${deviceBoard.sideToMove}")
        Log.d(TAG, "Now printing Legal available moves:-")
        this.legalMoves?.forEach { legalMove ->
            Log.d(TAG, legalMove.toString())
        }

    }

    override fun onGameOver(gameOverResponse: GameOverResponse) {
        Log.d(TAG, "GAME OVER")
        chessBoardViewModel.stopTimers()
        oldRatings = newRatings
        this.gameOverResponse = gameOverResponse
        this.isGameOver = true

        if(gameOverResponse.lastPlayerToMove != user?.id && gameOverResponse.move.from != "~" && gameOverResponse.move.to != "~"){
            Log.d(TAG, "Doing the move for the player who didn't make the last move...")
            if (gameOverResponse.move.promotion != null) {
                deviceBoard.doMove(
                    Move(
                        Constants.charToSquareMapping[gameOverResponse.move.from],
                        Constants.charToSquareMapping[gameOverResponse.move.to],
                        if (opponentColor == 'w')
                            Constants.whiteCharPieceToPiece[gameOverResponse.move.promotion.toCharArray()[0]]
                        else
                            Constants.blackCharPieceToPiece[gameOverResponse.move.promotion.toCharArray()[0]]
                    )
                )
            } else {
                deviceBoard.doMove(
                    Move(
                        Constants.charToSquareMapping[gameOverResponse.move.from],
                        Constants.charToSquareMapping[gameOverResponse.move.to]
                    )
                )
            }
            this.legalMoves = deviceBoard.legalMoves()
        }

        if(gameOverResponse.winningUser == user?.username){
            chessBoardViewModel.festive()
        }

        val move = gameOverResponse.move
        if ((gameOverResponse.result == ResultType.WHITE && color == 'w') || (gameOverResponse.result == ResultType.BLACK && color == 'b') || gameOverResponse.result == ResultType.DRAW) {
            return
        } else {
            if (move.promotion != null) {
                movesList.add(
                    BasicUtils.generateMoveString(
                        move.from,
                        move.to,
                        move.piece,
                        opponentColor,
                        move.promotion
                    )
                )
            } else {
                movesList.add(
                    BasicUtils.generateMoveString(
                        move.from,
                        move.to,
                        move.piece,
                        opponentColor
                    )
                )
            }
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


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
private fun RDPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        /*IconButton(
            onClick = {
                Log.d(TAG, "Resignation button clicked.")
                showResignDialog = true
            },
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(fieldBackground)
        ) {
            Icon(
                modifier = Modifier.padding(15.dp),
                painter = painterResource(R.drawable.ic_resign),
                contentDescription = null,
                tint = textColor
            )
        }*/
    }
    /*CenterAlignedTopAppBar(
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colorResource(R.color.game_background)
        ),
        title = {
            *//*Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {*//*
            Image(
                modifier = Modifier.size(200.dp),
                painter = painterResource(
                    when (GameMode.CASUAL) {
                        GameMode.CASUAL -> R.drawable.casual_banner
                        GameMode.RATED -> R.drawable.rated_banner
                    }
                ),
                contentDescription = null
            )
//            }
        }
    )*/
    /*Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        Log.d(TAG, "Resignation button clicked.")
//                showResignDialog = true
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(fieldBackground)
                ) {
                    Icon(
                        modifier = Modifier.padding(5.dp),
                        painter = painterResource(R.drawable.ic_resign),
                        contentDescription = null,
                        tint = textColor
                    )
                }
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    text = "Resign",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                    color = textColor
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        Log.d(TAG, "Draw button clicked.")
//                showDrawDialog = true
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(fieldBackground)
                ) {
                    Icon(
                        modifier = Modifier.padding(5.dp),
                        painter = painterResource(R.drawable.ic_draw),
                        contentDescription = null,
                        tint = textColor
                    )
                }
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    text = "Draw",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                    color = textColor
                )
            }
        }
        *//*Button(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = fieldBackground),
            onClick = {
                Log.d(TAG, "Resignation button clicked.")
//                showResignDialog = true
            }) {
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_resign),
                    tint = textColor,
                    contentDescription = null
                )

            }
        }*//*
        *//*Button(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = fieldBackground),
            onClick = {
                Log.d(TAG, "Draw button clicked.")
//                showDrawDialog = true
            }) {
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_draw),
                    tint = textColor,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(top = 3.dp),
                    text = "Draw",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }*//*
    }*/
}