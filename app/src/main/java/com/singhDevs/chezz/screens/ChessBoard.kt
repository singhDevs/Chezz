package com.singhDevs.chezz.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.CastleRight
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import com.google.gson.Gson
import com.singhDevs.chezz.R
import com.singhDevs.chezz.activities.ProfileActivity
import com.singhDevs.chezz.components.ResultDialog
import com.singhDevs.chezz.models.GameMode
import com.singhDevs.chezz.models.GameOverResponse
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.models.Message
import com.singhDevs.chezz.models.MessageTypes
import com.singhDevs.chezz.models.Ratings
import com.singhDevs.chezz.models.UserWithoutCreds
import com.singhDevs.chezz.network.User
import com.singhDevs.chezz.utils.BasicUtils
import com.singhDevs.chezz.utils.BasicUtils.getColor
import com.singhDevs.chezz.utils.Constants
import com.singhDevs.chezz.utils.Constants.FILES
import com.singhDevs.chezz.utils.Constants.charToSquareMapping
import com.singhDevs.chezz.viewmodels.ChessBoardViewModel
import kotlinx.coroutines.delay

private const val TAG = "ChessBoard"
private var legalMoves: List<Move>? = null

@Composable
fun ChessBoard(
    context: Context,
    viewModel: ChessBoardViewModel,
    user: User,
    opponent: UserWithoutCreds,
    color: Side = Side.WHITE,
    gameDuration: Int,
    gameType: GameType,
    gameMode: GameMode,
    deviceBoard: Board,
    legalBoardMoves: MutableList<Move>?,
    onPromotionSquareTapped: () -> Unit,
    showPromotionOptions: Boolean,
    onPromotionsOptionsDismiss: () -> Unit,
    gameOverResponse: GameOverResponse?,
    oldRatings: Ratings?,
    onMoveMade: (moveString: String) -> Unit,
    onNewGameClicked: () -> Unit,
    onExportPGNClicked: (toggleProgressIndicator: () -> Unit) -> Unit
) {
    Log.d(TAG, "ChessBoard() is called")
    if (legalBoardMoves?.isEmpty() == true || legalBoardMoves == null) {
        Log.d(TAG, "Found legalMoves empty!")
        legalMoves = deviceBoard.legalMoves()
    } else {
        legalMoves = legalBoardMoves
    }

    val textMeasurer = rememberTextMeasurer()

    var isFirstTap by remember { mutableStateOf(false) }
    var selectedSquare by remember { mutableStateOf<Square?>(null) }
    var selectedPiece by remember { mutableStateOf<PieceType?>(null) }
    var moveModel by remember { mutableStateOf<com.singhDevs.chezz.models.Move?>(null) }
    val promotedPiece by viewModel.promotedPiece.collectAsState()
    var pendingMove by remember { mutableStateOf<com.singhDevs.chezz.models.Move?>(null) }

    var castleQueen by remember { mutableStateOf(false) }
    var castleKing by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var isDialogDisplayed by remember { mutableStateOf(false) }
    val playerWhite = if (color == Side.WHITE) UserWithoutCreds(
        user.id,
        user.username,
        user.photoUrl,
        Ratings(
            1500,
            1500,
            1500
        )
    ) else opponent
    val playerBlack = if (color == Side.BLACK) UserWithoutCreds(
        user.id,
        user.username,
        user.photoUrl,
        Ratings(
            1500,
            1500,
            1500
        )
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

    LaunchedEffect(promotedPiece != Piece.NONE) {
        Log.d(TAG, "Entered promotedPiece LAUNCHED EFFECT")
        if (promotedPiece != Piece.NONE && pendingMove != null && moveModel != null) {
            Log.d(TAG, "Found a promoted piece! Final touches, and then sending to the server...")
            if (selectedPiece == null) {
                Log.d(TAG, "selectedPiece is null!")
                return@LaunchedEffect
            }
            val promotionMoveModel =
                pendingMove!!.copy(promotion = promotedPiece.fenSymbol.lowercase())
            val moveMessage = Gson().toJson(
                Message(
                    type = MessageTypes.MOVE.value,
                    move = promotionMoveModel,
                    piece = Constants.pieceTypeToChar[selectedPiece]
                )
            )
            onMoveMade(
                BasicUtils.generateMoveString(
                    moveModel!!.from,
                    moveModel!!.to,
                    Constants.pieceTypeToChar[selectedPiece]!!,
                    if (color == Side.WHITE) 'w' else 'b',
                    promotion = promotedPiece.fenSymbol.lowercase()
                )
            )
            deviceBoard.doMove(
                Move(
                    charToSquareMapping[moveModel!!.from],
                    charToSquareMapping[moveModel!!.to],
                    promotedPiece
                )
            )

            Log.d(
                TAG,
                "Sending move with promotion: ${promotedPiece.fenSymbol.lowercase()}"
            )
            Constants.webSocketClient.webSocket?.send(moveMessage)

            Log.d(
                TAG,
                "deviceBoard.sideToMove - Side to play next after promotion: ${deviceBoard.sideToMove}"
            )
            viewModel.resetPromotedPiece()
            selectedSquare = null
            selectedPiece = null
        }
    }

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
                        // Promotion options dialog is still open, closing it now...
                        if (showPromotionOptions) {
                            onPromotionsOptionsDismiss()
                            return@detectTapGestures
                        }

                        val squareWidth = size.width / 8
                        val x = (offset.x / squareWidth).toInt()
                        val y = (offset.y / squareWidth).toInt()

                        Log.d(TAG, "Size width: ${size.width}, height: ${size.height}")
                        Log.d(TAG, "Tap detected at x: ${offset.x}, y: ${offset.y}")
                        Log.d(TAG, "Square coordinates: x=$x, y=$y")

                        val tapSquare = if (color == Side.WHITE) {
                            charToSquareMapping[('a' + x) + (8 - y).toString()]
                        } else {
                            val file = FILES.reversed()[x]
                            val rank = y + 1
                            charToSquareMapping["$file$rank"]
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
                                    if (tappedPiece != null && tappedPiece.pieceSide != null && tappedPiece.pieceSide.name == color.name) {
                                        Log.d(TAG, "Selecting piece at $square")
                                        Log.d(TAG, "Selected Square in lowercase:  ${selectedSquare.toString().lowercase()}")
                                        selectedSquare = square
                                        selectedPiece = tappedPiece.pieceType
                                        isFirstTap = true
                                    }
                                }

                                // Second click - making a move
                                else -> {
                                    isFirstTap = false
                                    Log.d(
                                        TAG,
                                        "Second tap detected; deviceBoard.sideToMove: ${deviceBoard.sideToMove}\tcolor: $color"
                                    )
                                    if (deviceBoard.sideToMove != color) {
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
                                    moveModel = com.singhDevs.chezz.models.Move(
                                        from = selectedSquare!!
                                            .toString()
                                            .lowercase(),
                                        to = square
                                            .toString()
                                            .lowercase()
                                    )

                                    // Checking for a possible promotion move
                                    if (legalMoves!!.any { it.from == move.from && it.to == move.to }
                                        && legalMoves!!.filter { it.promotion != Piece.NONE }
                                            .any { it.from == move.from && it.to == move.to }
                                    ) {
                                        pendingMove = com.singhDevs.chezz.models.Move(
                                            from = selectedSquare!!.toString().lowercase(),
                                            to = square.toString().lowercase(),
                                            promotion = promotedPiece.name.lowercase()
                                        )
                                        onPromotionSquareTapped()
                                    } else if (legalMoves!!.any { it.from == move.from && it.to == move.to } && moveModel != null) {
                                        Log.d(TAG, "Found a Legal move!")
                                        Log.d(
                                            TAG,
                                            "Piece sent: $selectedPiece, char version: ${Constants.pieceTypeToChar[selectedPiece]}"
                                        )

                                        moveModel?.let { moveModel ->
                                            deviceBoard.doMove(
                                                Move(
                                                    charToSquareMapping[moveModel.from],
                                                    charToSquareMapping[moveModel.to]
                                                )
                                            )

                                            val castleRight = deviceBoard.getCastleRight(color)
                                            if (castleRight == CastleRight.NONE && (!castleKing && !castleQueen)) {
                                                /**
                                                 * WHITE CASTLING options --> e1c1 & e1g1
                                                 * BLACK CASTLING options --> e8c8 & e1g8
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
                                                onMoveMade(
                                                    if (castleQueen) "O-O-O"
                                                    else if (castleKing) "O-O"
                                                    else {
                                                        BasicUtils.generateMoveString(
                                                            moveModel.from,
                                                            moveModel.to,
                                                            Constants.pieceTypeToChar[selectedPiece]!!,
                                                            if (color == Side.WHITE) 'w' else 'b'
                                                        )
                                                    }
                                                )
                                            } else {
                                                onMoveMade(
                                                    BasicUtils.generateMoveString(
                                                        moveModel.from,
                                                        moveModel.to,
                                                        Constants.pieceTypeToChar[selectedPiece]!!,
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
                                            Log.d(
                                                TAG,
                                                "deviceBoard.sideToMove - Side to play next: ${deviceBoard.sideToMove}"
                                            )
                                        }
                                        selectedSquare = null
                                        selectedPiece = null
                                    } else {
                                        Log.d(
                                            TAG,
                                            "We are looking for" + move.from.toString() + move.to.toString() + ", which we couldn't find in the legalMoves list!"
                                        )
                                        Log.d(TAG, "LegalMoves: ")
                                        legalMoves!!.forEach { Log.d(TAG, it.toString()) }
                                        selectedSquare = null
                                        selectedPiece = null
                                    }
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
                        val file = FILES[col]
                        val squareKey = "$file$row"
                        val piece = deviceBoard.getPiece(charToSquareMapping[squareKey])

                        val isKingAttacked = deviceBoard.isKingAttacked() &&
                                deviceBoard.getSideToMove() == Side.WHITE &&
                                piece == Piece.WHITE_KING

                        if (isKingAttacked) {
                            // Player's king is checked, make that square RED
                            drawRect(
                                color = getColor(context, R.color.sign_out_btn_color),
                                topLeft = Offset(j * squareWidth, i * squareHeight),
                                size = Size(squareWidth, squareHeight)
                            )
                        } else {
                            // Draw squares normally
                            val fileIndex = FILES.indexOf(file)
                            val rankIndex = row - 1
                            val isDarkSquare = (fileIndex + rankIndex) % 2 == 0

                            if (isFirstTap && selectedSquare.toString().lowercase() == squareKey.lowercase()) {
                                drawRect(
                                    color = getColor(context, R.color.highlight_square),
                                    topLeft = Offset(j * squareWidth, i * squareHeight),
                                    size = Size(squareWidth, squareHeight)
                                )
                            } else {
                                drawRect(
                                    color = if (isDarkSquare)
                                        getColor(context, R.color.dark_square)
                                    else
                                        getColor(context, R.color.light_square),
                                    topLeft = Offset(j * squareWidth, i * squareHeight),
                                    size = Size(squareWidth, squareHeight)
                                )
                            }
                        }

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

                        // If it is the left-most file, write the rank numbers
                        if (col == 0) {
                            val fileIndex = FILES.indexOf(file)
                            val rankIndex = row - 1
                            val isDarkSquare = (fileIndex + rankIndex) % 2 == 0

                            drawText(
                                text = (row).toString(),
                                textMeasurer = textMeasurer,
                                topLeft = Offset(j * squareWidth, i * squareHeight),
                                style = TextStyle(
                                    color =
                                        if (isDarkSquare)
                                            getColor(context, R.color.text_light_square)
                                        else
                                            getColor(context, R.color.text_dark_square),
                                    fontSize = 10.sp
                                )
                            )
                        }

                        // If it is the bottom-most rank, write the file numbers
                        if (row == 1) {
                            val fileIndex = FILES.indexOf(file)
                            val rankIndex = 0
                            val isDarkSquare = (fileIndex + rankIndex) % 2 == 0

                            drawText(
                                text = file.toString(),
                                textMeasurer = textMeasurer,
                                topLeft = Offset(
                                    (j + 1) * squareWidth - 20,  // Right side with padding
                                    (i + 1) * squareHeight - 30  // Bottom with padding
                                ),
                                style = TextStyle(
                                    color =
                                        if (isDarkSquare)
                                            getColor(context, R.color.text_light_square)
                                        else
                                            getColor(context, R.color.text_dark_square),
                                    fontSize = 10.sp
                                )
                            )
                        }

                        // Draw piece
                        piece?.let {
                            map[it]?.let { bitmap ->
                                drawImage(
                                    image = bitmap,
                                    dstOffset = IntOffset(
                                        (j * squareWidth + 8).toInt(),
                                        (i * squareHeight + 8).toInt()
                                    ),
                                    dstSize = IntSize(
                                        squareWidth.toInt() - 15,
                                        squareHeight.toInt() - 15
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
                var col = 7

                for (i in 0..7) {
                    for (j in 0..7) {
                        val file = FILES[col]
                        val squareKey = "$file$row"
                        val piece = deviceBoard.getPiece(charToSquareMapping[squareKey])

                        val isKingAttacked = deviceBoard.isKingAttacked() &&
                                deviceBoard.getSideToMove() == Side.BLACK &&
                                piece == Piece.BLACK_KING

                        if (isKingAttacked) {
                            // Player's king is checked, make that square RED
                            drawRect(
                                color = getColor(context, R.color.sign_out_btn_color),
                                topLeft = Offset(j * squareWidth, i * squareHeight),
                                size = Size(squareWidth, squareHeight)
                            )
                        } else {
                            val fileIndex = FILES.indexOf(file)
                            val rankIndex = row - 1
                            val isDarkSquare = (fileIndex + rankIndex) % 2 == 0

                            if (isFirstTap && selectedSquare.toString().lowercase() == squareKey.lowercase()) {
                                drawRect(
                                    color = getColor(context, R.color.highlight_square),
                                    topLeft = Offset(j * squareWidth, i * squareHeight),
                                    size = Size(squareWidth, squareHeight)
                                )
                            } else {
                                drawRect(
                                    color = if (isDarkSquare)
                                        getColor(context, R.color.dark_square)
                                    else
                                        getColor(context, R.color.light_square),
                                    topLeft = Offset(j * squareWidth, i * squareHeight),
                                    size = Size(squareWidth, squareHeight)
                                )
                            }
                        }

                        // Draw highlight if square is selected
                        if (currentSelected != null) {
                            val selectedFile = currentSelected.toString()[0]
                            val selectedRank = currentSelected.toString()[1].digitToInt()

                            val selectedCol = FILES.reversed().indexOf(selectedFile.toString())
                            val selectedRow = selectedRank - 1

                            if (i == selectedRow && j == selectedCol) {
                                drawRect(
                                    color = Color(0xFF00FF00),
                                    topLeft = Offset(j * squareWidth, i * squareHeight),
                                    size = Size(squareWidth, squareHeight),
                                    alpha = 0.4f
                                )
                            }
                        }

                        // If it is the left-most file, write the rank numbers
                        if (col == 7) {
                            val fileIndex = FILES.indexOf(file)
                            val rankIndex = row - 1
                            val isDarkSquare = (fileIndex + rankIndex) % 2 == 0

                            drawText(
                                text = (row).toString(),
                                textMeasurer = textMeasurer,
                                topLeft = Offset(j * squareWidth, i * squareHeight),
                                style = TextStyle(
                                    color =
                                        if (isDarkSquare)
                                            getColor(context, R.color.text_light_square)
                                        else
                                            getColor(context, R.color.text_dark_square),
                                    fontSize = 10.sp
                                )
                            )
                        }

                        // If it is the bottom-most rank, write the file numbers
                        if (row == 8) {
                            val fileIndex = FILES.indexOf(file)
                            val rankIndex = row - 1
                            val isDarkSquare = (fileIndex + rankIndex) % 2 == 0

                            drawText(
                                text = file.toString(),
                                textMeasurer = textMeasurer,
                                topLeft = Offset(
                                    (j + 1) * squareWidth - 20,  // Right side with padding
                                    (i + 1) * squareHeight - 30  // Bottom with padding
                                ),
                                style = TextStyle(
                                    color =
                                        if (isDarkSquare)
                                            getColor(context, R.color.white)
                                        else
                                            getColor(context, R.color.black),
                                    fontSize = 10.sp
                                )
                            )
                        }

                        // Draw piece
                        piece?.let {
                            map[it]?.let { bitmap ->
                                drawImage(
                                    image = bitmap,
                                    dstOffset = IntOffset(
                                        (j * squareWidth + 8).toInt(),
                                        (i * squareHeight + 8).toInt()
                                    ),
                                    dstSize = IntSize(
                                        squareWidth.toInt() - 15,
                                        squareHeight.toInt() - 15
                                    )
                                )
                            }
                        }
                        col--
                    }
                    col = 7
                    row++
                }
            }
        }
    }

    if (gameOverResponse != null && !isDialogDisplayed) {
        showResultDialog = true
    }

    if (showResultDialog) {
        if (gameOverResponse == null) {
            Log.d(TAG, "gameOverResponse is null!")
            return
        }
        if (gameOverResponse.updatedRatings == null && gameMode == GameMode.RATED) {
            Log.d(TAG, "updatedRatings is null!")
            return
        }

        val newRating = if (gameOverResponse.updatedRatings != null) {
            when (gameType) {
                GameType.BULLET -> gameOverResponse.updatedRatings.bulletRating
                GameType.RAPID -> gameOverResponse.updatedRatings.rapidRating
                GameType.BLITZ -> gameOverResponse.updatedRatings.blitzRating
            }
        } else null

        val oldRating = if (oldRatings != null) {
            when (gameType) {
                GameType.BULLET -> oldRatings.bulletRating
                GameType.RAPID -> oldRatings.rapidRating
                GameType.BLITZ -> oldRatings.blitzRating
            }
        } else null

        ResultDialog(
            context,
            gameOverResponse.result,
            gameOverResponse.cause,
            newRating,
            oldRating,
            gameType,
            gameDuration,
            playerWhite,
            playerBlack,
            gameOverResponse.winningUser,
            user.username,
            viewModel,
            onDismissRequest = {
                Log.d(TAG, "onDismissRequest called.")
                isDialogDisplayed = true
                showResultDialog = false
            },
            onViewOpponentProfileClicked = {
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("userId", opponent.id)
                context.startActivity(intent)
            },
            onNewGameClicked = onNewGameClicked,
            onExportPGNClicked = { toggleProgressIndicator ->
                onExportPGNClicked(toggleProgressIndicator)
            }
        )
    }
}