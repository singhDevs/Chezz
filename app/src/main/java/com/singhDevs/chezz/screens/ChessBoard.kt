package com.singhDevs.chezz.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.singhDevs.chezz.Constants
import com.singhDevs.chezz.Constants.alphabets
import com.singhDevs.chezz.Constants.squareMapping
import com.singhDevs.chezz.R
import com.singhDevs.chezz.models.Message
import com.singhDevs.chezz.models.MessageTypes
import com.singhDevs.chezz.models.chessboard.Board
import com.singhDevs.chezz.models.chessboard.Square
import com.singhDevs.chezz.utils.BasicUtils.getColor
import com.singhDevs.chezz.websocket.MessageActions
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.move.Move
import com.google.gson.Gson

private const val TAG = "WebSocketClient"

@Composable
fun ChessBoard(
    modifier: Modifier = Modifier,
    messageActions: MessageActions,
    color: Char = 'w',
    context: Context,
    board: Board,
    deviceBoard: com.github.bhlangonijr.chesslib.Board,
    legalMoves: List<Move>
) {
    Log.d(TAG, "ChessBoard() is called")
    var selectedSquare by remember { mutableStateOf<com.github.bhlangonijr.chesslib.Square?>(null) }
    var selectedPiece by remember { mutableStateOf<Square?>(null) }
    val map: MutableMap<Piece, ImageBitmap> = mutableMapOf()
    map[Piece.BLACK_BISHOP] = ImageBitmap.imageResource(id = R.drawable.bb)
    map[Piece.BLACK_KNIGHT] = ImageBitmap.imageResource(id = R.drawable.bn)
    map[Piece.BLACK_ROOK] = ImageBitmap.imageResource(id = R.drawable.br)
    map[Piece.BLACK_ROOK] = ImageBitmap.imageResource(id = R.drawable.bk)
    map[Piece.BLACK_QUEEN] = ImageBitmap.imageResource(id = R.drawable.bq)
    map[Piece.BLACK_PAWN] = ImageBitmap.imageResource(id = R.drawable.bp)
    map[Piece.WHITE_BISHOP] = ImageBitmap.imageResource(id = R.drawable.wb)
    map[Piece.WHITE_KNIGHT] = ImageBitmap.imageResource(id = R.drawable.wn)
    map[Piece.WHITE_ROOK] = ImageBitmap.imageResource(id = R.drawable.wr)
    map[Piece.WHITE_KING] = ImageBitmap.imageResource(id = R.drawable.wk)
    map[Piece.WHITE_QUEEN] = ImageBitmap.imageResource(id = R.drawable.wq)
    map[Piece.WHITE_PAWN] = ImageBitmap.imageResource(id = R.drawable.wp)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .aspectRatio(1 / 1f)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            val squareWidth = size.width / 8
                            val x = (it.x / squareWidth).toInt()
                            val y = (it.y / squareWidth).toInt()
                            val piece =
                                if (color == 'b') board._board[y * 16 + x]
                                else board._board[(7 - y) * 16 + x]

                            if (piece != null) {
//                                Log.d(TAG, "New selected piece: $piece")
                                if (selectedPiece == null) {
                                    /**
                                     * No piece selected previously
                                     */
                                    selectedPiece = piece
                                    selectedSquare =
                                        squareMapping[alphabets[x] + (y + 1).toString()]
                                    Log.d(TAG, "selected piece set to: $selectedPiece")
                                } else {
                                    /**
                                     * Some piece has already been selected
                                     * 1. If the new selected piece is of the same color, update the selected piece.
                                     * 2. Else validate the move.
                                     */
                                    if (piece.color == selectedPiece!!.color) {
                                        selectedPiece = piece
                                        Log.d(
                                            TAG,
                                            "selected piece set to: $selectedPiece\tselected square: $selectedSquare"
                                        )
                                    } else {
                                        /**
                                         * Do validation here
                                         */
                                        legalMoves.forEach { move ->
                                            Log.d(
                                                TAG,
                                                "move.from: ${move.from}\tmove.to: ${move.to}"
                                            )
                                            Log.d(TAG, "selected square: $selectedSquare")
                                            Log.d(
                                                TAG,
                                                "selectedPiece: $selectedPiece\tPiece: $piece"
                                            )
                                            val from = move.from
                                                .toString()
                                                .lowercase()
                                            val to = move.to
                                                .toString()
                                                .lowercase()
                                            val toSquare =
                                                if (color == 'w') squareMapping[alphabets[x] + (y + 1).toString()]
                                                else squareMapping[alphabets[x] + (8 - y).toString()]

                                            val toSquareString = toSquare
                                                .toString()
                                                .lowercase()
                                            Log.d(TAG, "toSquareString: $toSquareString")

                                            if (squareMapping[from] == selectedSquare && to == toSquareString) {
                                                // Found a valid move
                                                Log.d(TAG, "Valid move found, $move")
//                                                deviceBoard.doMove(move)
                                                messageActions.onMoveMade(board, Move(selectedSquare, toSquare))
                                                selectedPiece = null
                                                return@forEach
                                            } else {
                                                // Not a valid move
                                                Log.i(TAG, "Not a valid move!")
                                            }
                                        }
                                    }
                                }
                            } else {
                                Log.d(TAG, "New selected piece is null!")
                                if (selectedPiece != null) {
                                    /**
                                     * Do validation here
                                     */
                                    val toSquare =
                                        if (color == 'b') squareMapping[alphabets[x] + (y + 1).toString()]
                                        else squareMapping[alphabets[x] + (8 - y).toString()]

                                    val toSquareString = toSquare
                                        .toString()
                                        .lowercase()

                                    Log.d(
                                        TAG,
                                        "selectedPiece color: ${selectedPiece!!.color}\tselectedPiece type: ${selectedPiece!!.type}"
                                    )
                                    Log.d(TAG, "selectedPiece: ${selectedPiece.toString()}")
                                    Log.d(TAG, "toSquare: $toSquare")
                                    Log.d(TAG, "Printing legal moves:-")
                                    legalMoves.forEach{ legalMove ->
                                        Log.d(TAG, legalMove.toString())
                                    }

                                    if(legalMoves.isEmpty()) Log.i(TAG, "legal moces is empty!")

                                    legalMoves.forEach { move ->
                                        val from = move.from
                                            .toString()
                                            .lowercase()
                                        val to = move.to
                                            .toString()
                                            .lowercase()
                                        Log.d(
                                            TAG,
                                            "move.from: ${from}\tselectedSquare: $selectedSquare\tmove.to: ${to}\ttoSquareString: $toSquareString"
                                        )

                                        if (squareMapping[from] == selectedSquare && to == toSquareString) {
                                            // Found a valid move
                                            Log.d(TAG, "Valid move found, $move")
                                            deviceBoard.doMove(move)
                                            messageActions.onMoveMade(board, Move(selectedSquare, toSquare))
                                            selectedPiece = null
                                            return@forEach
                                        } else {
                                            // Not a valid move
                                            Log.d(TAG, "Not a valid move!")
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "selected piece is NULL!")
                                }
                            }
                        }
                    }
                    .drawWithCache {
                        onDrawBehind {
                            val squareWidth = size.width / 8
                            val squareHeight = size.width / 8

                            drawRect(
                                color = Color.Black,
                                topLeft = Offset(0f, 0f),
                                size = Size(size.width, size.height),
                                style = Stroke(width = 5.dp.toPx())
                            )
                            Log.d(TAG, "color i got: $color")
                            if (color == 'w') {
                                var row = 8
                                var col = 0
                                for (i in 0..7) {
                                    for (j in 0..7) {
                                        drawRect(
                                            color = if ((row + col) % 2 == 0) getColor(
                                                context,
                                                R.color.light_square
                                            ) else getColor(context, R.color.dark_square),
                                            topLeft = Offset(j * squareWidth, i * squareHeight),
                                            size = Size(squareWidth, squareHeight)
                                        )

                                        val piece = deviceBoard.getPiece(squareMapping[alphabets[col] + row.toString()])
                                        if (piece != null) {
                                            Log.d(TAG, "placing piece: $piece")
                                            val image = map[piece]
                                            image?.let {
                                                drawImage(
                                                    image = it,
                                                    topLeft = Offset(
                                                        j * squareWidth,
                                                        i * squareHeight
                                                    )
                                                )
                                            }
                                        } else {
                                            Log.d(TAG, "Square is NULL, i: $i\tj: $j")
                                        }
                                        col++
                                    }
                                    row--
                                    col = 0
                                }
                            } else {
                                var row = 1
                                var col = 0
                                for (i in 0..7) {
                                    for (j in 0..7) {
                                        drawRect(
                                            color = if ((i + j) % 2 == 0) getColor(
                                                context,
                                                R.color.light_square
                                            ) else getColor(context, R.color.dark_square),
                                            topLeft = Offset(j * squareWidth, i * squareHeight),
                                            size = Size(squareWidth, squareHeight)
                                        )

                                        Log.d(TAG, "Board is not NULL, i: $i\tj: $j")
                                        val piece =
                                            deviceBoard.getPiece(squareMapping[alphabets[col] + row.toString()])
                                        if (piece != null) {
                                            Log.d(TAG, "placing piece: $piece")
                                            val image = map[piece]
                                            image?.let {
                                                drawImage(
                                                    image = it,
                                                    topLeft = Offset(
                                                        j * squareWidth,
                                                        i * squareHeight
                                                    )
                                                )
                                            }
                                        } else {
                                            Log.d(TAG, "Square is NULL, i: $i\tj: $j")
                                        }
                                        col++
                                    }
                                    col = 0
                                    row++
                                }
                            }
                        }
                    }
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val moveMessage = Gson().toJson(Message(type = MessageTypes.INIT_GAME.value))
                Button(onClick = {
                    Constants.webSocketClient.webSocket?.send(moveMessage)
                }) {
                    Text(text = "Send moves")
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