package com.singhDevs.chezz.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.github.bhlangonijr.chesslib.CastleRight
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import com.google.gson.Gson
import com.singhDevs.chezz.R
import com.singhDevs.chezz.models.Message
import com.singhDevs.chezz.models.MessageTypes
import com.singhDevs.chezz.models.chessboard.Board
import com.singhDevs.chezz.models.chessboard.Castling
import com.singhDevs.chezz.utils.BasicUtils.getColor
import com.singhDevs.chezz.utils.Constants
import com.singhDevs.chezz.utils.Constants.alphabets
import com.singhDevs.chezz.utils.Constants.charToSquareMapping
import com.singhDevs.chezz.websocket.MessageActions

private const val TAG = "WebSocketClient"
private var legalMoves: List<Move>? = null

@Composable
fun ChessBoard(
    modifier: Modifier = Modifier,
    messageActions: MessageActions,
    color: Side = Side.WHITE,
    username: String,
    opponent: String,
    context: Context,
    board: Board,
    deviceBoard: com.github.bhlangonijr.chesslib.Board,
    legalBoardMoves: MutableList<Move>?,
    result: Char?,
    cause: String?,
    onMoveMade: (from: String, to: String, piece: Char) -> Unit
) {
    Log.d("Chezz", "result: $result")
    Log.d(TAG, "ChessBoard() is called")

    if (legalBoardMoves?.isEmpty() == true || legalBoardMoves == null) {
        Log.d(TAG, "Found legalMoves empty!")
        legalMoves = deviceBoard.legalMoves()
    } else legalMoves = legalBoardMoves

    var selectedSquare by remember { mutableStateOf<Square?>(null) }
    var selectedPiece by remember { mutableStateOf<PieceType?>(null) }

    var castleRight by remember { mutableStateOf<CastleRight?>(CastleRight.NONE)}

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
                                        castleRight = deviceBoard.getCastleRight(color)
                                        moveModel.kingSideCastle = castleRight == CastleRight.KING_SIDE
                                        moveModel.queenSideCastle = castleRight == CastleRight.QUEEN_SIDE

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


    if (result != null && cause != null) {
        val white: Pair<String, String>
        val black: Pair<String, String>

        if (color == Side.WHITE) {
            white = Pair(username, "")
            black = Pair(opponent, "")
        } else {
            white = Pair(opponent, "")
            black = Pair(username, "")
        }
        MinimalDialog(result, cause, white, black)
    }
}

@Preview
@Composable
private fun MinimalDialogPreview() {
    MinimalDialog('w', "CHECKMATE", Pair("plutamite", ""), Pair("benzabyte", ""))
}

@Composable
fun MinimalDialog(
    result: Char,
    cause: String,
    playerWhite: Pair<String, String>,
    playerBlack: Pair<String, String>
) {
    Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // Background Card with blur effect
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Box {
                    // Blurred background image
                    Image(
                        painter = painterResource(R.drawable.chess_wallpaper),
                        contentDescription = "Chess Background",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix().apply {
                                setToScale(0.5f, 0.5f, 0.5f, 1f) // Reduce RGB values to 60%
                            }
                        ),
                        modifier = Modifier
                            .matchParentSize()
                            .blur(radius = 10.dp)
                            .alpha(0.95f)
                    )

                    // Content overlay with semi-transparent background
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.3f))
                            .padding(20.dp, 50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = cause,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            val lottieAnimation by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(
                                    R.raw.popper_lottie
                                )
                            )

                            Box {
                                if (result == 'w') {
                                    LottieAnimation(
                                        lottieAnimation,
                                        iterations = 100,
                                        isPlaying = true,
                                        restartOnPlay = true,
                                        modifier = Modifier.wrapContentSize()
                                    )
                                }
                                Column(
                                    modifier = Modifier.wrapContentSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val borderWidth = if (result == 'w') 5.dp else 0.dp
                                    val borderColor = if (result == 'w') getColor(
                                        LocalContext.current,
                                        R.color.golden
                                    ) else Color.Black
                                    Image(
                                        painter = painterResource(R.drawable.chesslogo),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(0.dp)
                                            .clip(CircleShape)
                                            .border(borderWidth, borderColor, CircleShape)
                                    )
                                    Text(
                                        text = playerWhite.first,
                                        textAlign = TextAlign.Center,
                                        fontSize = 22.sp,
                                        color = Color.White
                                    )
                                }

                            }
                            Text(
                                modifier = Modifier.wrapContentSize(),
                                text = "vs",
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Box {
                                if (result == 'b') {
                                    LottieAnimation(
                                        lottieAnimation,
                                        iterations = 100,
                                        isPlaying = true,
                                        restartOnPlay = true,
                                        modifier = Modifier.wrapContentSize()
                                    )
                                }
                                Column(
                                    modifier = Modifier.wrapContentSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val borderWidth = if (result == 'b') 5.dp else 0.dp
                                    val borderColor = if (result == 'b') getColor(
                                        LocalContext.current,
                                        R.color.golden
                                    ) else Color.Black
                                    Image(
                                        painter = painterResource(R.drawable.chesslogo),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(0.dp)
                                            .clip(CircleShape)
                                            .border(borderWidth, borderColor, CircleShape)
                                    )
                                    Text(
                                        text = playerBlack.first,
                                        textAlign = TextAlign.Center,
                                        fontSize = 22.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Text(
                            text = when (result) {
                                'w' -> playerWhite.first + " won!"
                                'b' -> playerBlack.first + " won!"
                                'r' -> cause
                                else -> "Draw"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(0.dp, 20.dp),
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
//@Preview(showSystemUi = true)
//@Composable
//private fun ChessBoardPreview() {
//    ChessBoard(LocalContext.current)
//}