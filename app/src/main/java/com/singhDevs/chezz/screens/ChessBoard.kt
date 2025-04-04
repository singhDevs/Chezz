package com.singhDevs.chezz.screens

import android.content.Context
import android.os.WorkDuration
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.CastleRight
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import com.google.gson.Gson
import com.singhDevs.chezz.R
import com.singhDevs.chezz.UserRatingsOuterClass
import com.singhDevs.chezz.components.ResultDialog
import com.singhDevs.chezz.di.RatingsRepository
import com.singhDevs.chezz.models.GameOverResponse
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.models.Message
import com.singhDevs.chezz.models.MessageTypes
import com.singhDevs.chezz.models.ResultType
import com.singhDevs.chezz.models.User
import com.singhDevs.chezz.network.RetrofitClient
import com.singhDevs.chezz.utils.BasicUtils
import com.singhDevs.chezz.utils.BasicUtils.getColor
import com.singhDevs.chezz.utils.Constants
import com.singhDevs.chezz.utils.Constants.alphabets
import com.singhDevs.chezz.utils.Constants.charToSquareMapping
import com.singhDevs.chezz.viewmodels.ChessBoardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "WebSocketClient"
private var legalMoves: List<Move>? = null
private var promotionMoves: MutableList<Move> = arrayListOf()

@Composable
fun ChessBoard(
    modifier: Modifier,
    user: com.singhDevs.chezz.network.User,
    opponent: User,
    color: Side = Side.WHITE,
    gameDuration: Int,
    gameType: GameType,
    context: Context,
    deviceBoard: Board,
    turn: Side,
    changeTurn: () -> Unit,
    legalBoardMoves: MutableList<Move>?,
    result: ResultType?,
    cause: String?,
    gameOverResponse: GameOverResponse?,
    viewModel: ChessBoardViewModel,
    onMoveMade: (move: String) -> Unit,
    onExportPGNClicked: (toggleProgressIndicator: () -> Unit) -> Unit
) {
    Log.d("Chezz", "result: $result")
    Log.d(TAG, "ChessBoard() is called")

    if (legalBoardMoves?.isEmpty() == true || legalBoardMoves == null) {
        Log.d(TAG, "Found legalMoves empty!")
        legalMoves = deviceBoard.legalMoves()
    } else {
        legalMoves = legalBoardMoves
    }
    legalMoves?.forEach {
        if (!Piece.NONE.equals(it.promotion)) {
            promotionMoves.add(it)
        }
    }

    var selectedSquare by remember { mutableStateOf<Square?>(null) }
    var selectedPiece by remember { mutableStateOf<PieceType?>(null) }

    var castleQueen by remember { mutableStateOf(false) }
    var castleKing by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    val playerWhite = if (color == Side.WHITE) User(
        user.username, 1500,
        1500,
        1500,
        user.photoUrl
    ) else opponent
    val playerBlack = if (color == Side.BLACK) User(
        user.username, 1500,
        1500,
        1500,
        user.photoUrl
    ) else opponent

    LaunchedEffect(selectedSquare) {
        Log.d(TAG, "Selected square changed to: $selectedSquare")
    }

    // Piece images mapping
    val map: MutableMap<Piece, ImageBitmap> = mutableMapOf()
    map[Piece.BLACK_BISHOP] = ImageBitmap.imageResource(id = R.drawable.bb)
    map[Piece.BLACK_KNIGHT] = ImageBitmap.imageResource(id = R.drawable.bn)
    map[Piece.BLACK_ROOK] = ImageBitmap.imageResource(id = R.drawable.br)
    map[Piece.BLACK_KING] = ImageBitmap.imageResource(id = R.drawable.bk)
    map[Piece.BLACK_QUEEN] = ImageBitmap.imageResource(id = R.drawable.bq)
    map[Piece.BLACK_PAWN] = ImageBitmap.imageResource(id = R.drawable.bp)
    map[Piece.WHITE_BISHOP] = ImageBitmap.imageResource(id = R.drawable.wb)
    map[Piece.WHITE_KNIGHT] = ImageBitmap.imageResource(id = R.drawable.wn)
    map[Piece.WHITE_ROOK] = ImageBitmap.imageResource(id = R.drawable.wr)
    map[Piece.WHITE_KING] = ImageBitmap.imageResource(id = R.drawable.wk)
    map[Piece.WHITE_QUEEN] = ImageBitmap.imageResource(id = R.drawable.wq)
    map[Piece.WHITE_PAWN] = ImageBitmap.imageResource(id = R.drawable.wp)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val squareWidth = size.width / 8
                        val x = (offset.x / squareWidth).toInt()
                        val y = (offset.y / squareWidth).toInt()

                        Log.d(TAG, "Size width: ${size.width}, height: ${size.height}")
                        Log.d(TAG, "Tap detected at x: ${offset.x}, y: ${offset.y}")
                        Log.d(TAG, "Square coordinates: x=$x, y=$y")

                        val tapSquare = if (color == Side.WHITE) {
                            charToSquareMapping[('a' + x) + (8 - y).toString()]
                        } else {
                            charToSquareMapping[('a' + x) + (y + 1).toString()]
                        }

                        Log.d(TAG, "Calculated tapSquare: $tapSquare")
                        Log.d(TAG, "Current selectedSquare: $selectedSquare")
                        Log.d(TAG, "Player color: $color")

                        tapSquare?.let { square ->
                            val tappedPiece = deviceBoard.getPiece(square)
                            Log.d(TAG, "Tapped piece: $tappedPiece")

                            when {
                                // First click - selecting a piece
                                selectedSquare == null -> {
                                    Log.d(TAG, "First click detected!")
                                    if (tappedPiece != null && tappedPiece.pieceSide != null && tappedPiece.pieceSide.name == color.name) {
                                        Log.d(TAG, "Selecting piece at $square")
                                        selectedSquare = square
                                        selectedPiece = tappedPiece.pieceType
                                    }
                                }
                                // Second click - making a move
                                else -> {
                                    Log.d(TAG, "Second click detected!")
                                    Log.d(TAG, "turn: $turn\tcolor: $color")
                                    if (turn != color) {
                                        Log.d(TAG, "Not our turn yet!")
                                        return@detectTapGestures
                                    }

                                    val move = Move(
                                        charToSquareMapping[selectedSquare!!
                                            .toString()
                                            .lowercase()],
                                        charToSquareMapping[square
                                            .toString()
                                            .lowercase()]
                                    )
                                    val moveModel = com.singhDevs.chezz.models.Move(
                                        from = selectedSquare!!
                                            .toString()
                                            .lowercase(),
                                        to = square
                                            .toString()
                                            .lowercase()
                                    )

                                    /*Log.d(
                                        TAG,
                                        "Need to find any promotion moves available: ${move.from} -> ${move.to}..."
                                    )
                                    if (promotionMoves.any { it.from == move.from && it.to == move.to }) {
                                        Log.d(TAG, "Didn't find any promotion moves!")
                                    }
                                    else {
                                        Log.d(TAG, "Found a promotion move!")

                                        promotionDialogOffset = Pair(x, y)
                                        shouldShowPromotionDialog = true

                                        deviceBoard.doMove(
                                            Move(
                                                charToSquareMapping[moveModel.from],
                                                charToSquareMapping[moveModel.to]
                                            )
                                        )
                                        castleRight = deviceBoard.getCastleRight(color)
                                        moveModel.kingSideCastle =
                                            castleRight == CastleRight.KING_SIDE
                                        moveModel.queenSideCastle =
                                            castleRight == CastleRight.QUEEN_SIDE

                                        Log.d(TAG, "castleRight: $castleRight")

                                        val moveMessage = Gson().toJson(
                                            Message(
                                                type = MessageTypes.MOVE.value,
                                                move = moveModel,
                                                piece = Constants.pieceTypeToChar[selectedPiece]
                                            )
                                        )
                                        onMoveMade(
                                            moveModel.from,
                                            moveModel.to,
                                            Constants.pieceTypeToChar[selectedPiece]!!.toCharArray()[0]
                                        )
                                        Constants.webSocketClient.webSocket?.send(moveMessage)

                                        selectedSquare = null
                                        selectedPiece = null
                                        promotionMoves.clear()
                                        return@detectTapGestures
                                    }*/

                                    Log.d(
                                        TAG,
                                        "Need to find legal moves for: ${move.from} -> ${move.to}..."
                                    )
                                    if (legalMoves!!.any { it.from == move.from && it.to == move.to }) {
                                        Log.d(TAG, "Found a Legal move!")
                                        Log.d(
                                            TAG,
                                            "Piece sent: $selectedPiece, char version: ${Constants.pieceTypeToChar[selectedPiece]}"
                                        )
                                        deviceBoard.doMove(
                                            Move(
                                                charToSquareMapping[moveModel.from],
                                                charToSquareMapping[moveModel.to]
                                            )
                                        )

                                        val castleRight = deviceBoard.getCastleRight(color)
                                        if (castleRight == CastleRight.NONE && (!castleKing && !castleQueen)) {
                                            /*
                                            WHITE CASTLING options --> e1c1 & e1g1
                                            BLACK CASTLING options --> e8c8 & e1g8
                                            */
                                            if (color == Side.WHITE) {
                                                if (moveModel.from == "e1" && moveModel.to == "c1") {
                                                    castleQueen = true
                                                    moveModel.queenSideCastle = true
                                                } else if (moveModel.from == "e1" && moveModel.to == "g1") {
                                                    castleKing = true
                                                    moveModel.kingSideCastle = true
                                                }
                                            } else {
                                                if (moveModel.from == "e8" && moveModel.to == "c8") {
                                                    castleQueen = true
                                                    moveModel.queenSideCastle = true
                                                } else if (moveModel.from == "e8" && moveModel.to == "g8") {
                                                    castleKing = true
                                                    moveModel.kingSideCastle = true
                                                }
                                            }
                                            onMoveMade(if (castleQueen) "O-O-O" else "O-O")
                                        } else {
                                            onMoveMade(
                                                BasicUtils.generateMoveString(
                                                    moveModel.from,
                                                    moveModel.to,
                                                    Constants.pieceTypeToChar[selectedPiece]!!.toCharArray()[0],
                                                    if (color == Side.WHITE) 'w' else 'b'
                                                )
                                            )
                                        }

                                        Log.d(TAG, "castleRight: $castleRight")

                                        val moveMessage = Gson().toJson(
                                            Message(
                                                type = MessageTypes.MOVE.value,
                                                move = moveModel,
                                                piece = Constants.pieceTypeToChar[selectedPiece]
                                            )
                                        )
                                        Constants.webSocketClient.webSocket?.send(moveMessage)
                                        changeTurn()
                                    } else {
                                        Log.d(TAG, "Didn't find any Legal move!")
                                        if (legalMoves!!.isEmpty()) {
                                            Log.d(TAG, "Found legalMoves empty!")
                                        } else {
                                            Log.d(TAG, "Printing all legal moves...")
                                            for (m in legalMoves!!) {
                                                Log.d(TAG, "${m.from} -> ${m.to}")
                                            }
                                        }
                                    }
                                    selectedSquare = null
                                    selectedPiece = null
                                }
                            }
                        }
                    }
                }
        ) {
            val currentSelected = selectedSquare // Force recomposition

            // Draw board border
            drawRect(
                color = Color.Black,
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height),
                style = Stroke(width = 5.dp.toPx())
            )

            val squareWidth = size.width / 8
            val squareHeight = size.height / 8

            if (color == Side.WHITE) {
                var row = 8
                var col = 0

                for (i in 0..7) {
                    for (j in 0..7) {
                        // Draw square
                        drawRect(
                            color = if ((row + col) % 2 == 0)
                                getColor(context, R.color.light_square)
                            else
                                getColor(context, R.color.dark_square),
                            topLeft = Offset(j * squareWidth, i * squareHeight),
                            size = Size(squareWidth, squareHeight)
                        )

//                        if(shouldShowPromotionDialog){
//                            drawRect(
//                                color = Color.White,
//                                topLeft = Offset(j * squareWidth, i * squareHeight),
//                                size = Size(squareWidth, squareHeight)
//                            )
//                        }

                        // Draw highlight if square is selected
                        if (selectedSquare != null) {
                            val selectedCol = selectedSquare.toString()[0] - 'a'
                            val selectedRow = 8 - selectedSquare.toString()[1].digitToInt()
                            if (i == selectedRow && j == selectedCol) {
                                drawRect(
                                    color = Color(0xFF00FF00),
                                    topLeft = Offset(j * squareWidth, i * squareHeight),
                                    size = Size(squareWidth, squareHeight),
                                    alpha = 0.4f
                                )
                            }
                        }

                        // Draw piece
                        val piece = deviceBoard.getPiece(
                            charToSquareMapping[alphabets[col] + row.toString()]
                        )
                        piece?.let {
                            map[it]?.let { bitmap ->
                                drawImage(
                                    image = bitmap,
                                    dstOffset = IntOffset(
                                        (j * squareWidth).toInt(),
                                        (i * squareHeight).toInt()
                                    ),
                                    dstSize = IntSize(
                                        squareWidth.toInt(),
                                        squareHeight.toInt()
                                    )
                                )
                            }
                        }

                        col++
                    }
                    row--
                    col = 0
                }
            } else {
                // Black perspective
                var row = 1
                var col = 0

                for (i in 0..7) {
                    for (j in 0..7) {
                        drawRect(
                            color = if ((i + j) % 2 == 0)
                                getColor(context, R.color.light_square)
                            else
                                getColor(context, R.color.dark_square),
                            topLeft = Offset(j * squareWidth, i * squareHeight),
                            size = Size(squareWidth, squareHeight)
                        )

                        // Draw highlight if square is selected
                        if (currentSelected != null) {
                            val selectedCol = selectedSquare.toString()[0] - 'a'
                            val selectedRow = selectedSquare.toString()[1].digitToInt() - 1
                            if (i == selectedRow && j == selectedCol) {
                                drawRect(
                                    color = Color(0xFF00FF00),
                                    topLeft = Offset(j * squareWidth, i * squareHeight),
                                    size = Size(squareWidth, squareHeight),
                                    alpha = 0.4f
                                )
                            }
                        }

                        val piece = deviceBoard.getPiece(
                            charToSquareMapping[alphabets[col] + row.toString()]
                        )
                        piece?.let {
                            map[it]?.let { bitmap ->
                                drawImage(
                                    image = bitmap,
                                    dstOffset = IntOffset(
                                        (j * squareWidth).toInt(),
                                        (i * squareHeight).toInt()
                                    ),
                                    dstSize = IntSize(
                                        squareWidth.toInt(),
                                        squareHeight.toInt()
                                    )
                                )
                            }
                        }

                        col++
                    }
                    col = 0
                    row++
                }
            }
        }
    }

    if (gameOverResponse != null) {
        showResultDialog = true
    }

    if (showResultDialog) {
        if (gameOverResponse == null) {
            Log.d(TAG, "gameOverResponse is null!")
            return
        }
        if (gameOverResponse.updatedRatings == null) {
            Log.d(TAG, "updatedRatings is null!")
            return
        }
        val newRatings = when (gameType) {
            GameType.BULLET -> gameOverResponse.updatedRatings.bulletRating
            GameType.RAPID -> gameOverResponse.updatedRatings.rapidRating
            GameType.BLITZ -> gameOverResponse.updatedRatings.blitzRating
        }
        ResultDialog(
            context,
            gameOverResponse.result,
            gameOverResponse.cause,
            newRatings,
            gameType,
            gameDuration,
            playerWhite,
            playerBlack,
            gameOverResponse.winningUser,
            user.username,
            viewModel,
            onDismissRequest = {
                Log.d(TAG, "onDismissRequest called.")
                showResultDialog = false
            },
            onShareClicked = {

            },
            onRematchClicked = {

            },
            onNewGameClicked = {

            },
            onExportPGNClicked = { toggleProgressIndicator ->
                onExportPGNClicked(toggleProgressIndicator)
            }
        )

        //Updating user ratings
        Constants.user = com.singhDevs.chezz.network.User(
            id = Constants.user.id,
            email = Constants.user.email,
            username = Constants.user.username,
            photoUrl = Constants.user.photoUrl,
            ratings = gameOverResponse.updatedRatings
        )

    }
}