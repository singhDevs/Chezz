package com.singhDevs.chezz.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import com.singhDevs.chezz.R
import com.singhDevs.chezz.activities.ReplayGameActivity
import com.singhDevs.chezz.models.Ratings
import com.singhDevs.chezz.models.UserWithoutCreds
import com.singhDevs.chezz.models.User
import com.singhDevs.chezz.utils.BasicUtils.getColor
import com.singhDevs.chezz.utils.Constants.FILES
import com.singhDevs.chezz.utils.Constants.charToSquareMapping

private const val TAG = "ReplayChessBoard"

@Composable
fun ReplayChessBoard(
    context: ReplayGameActivity,
    color: Side,
    deviceBoard: Board,
    movesStringList: List<String>,
    currIndex: Int
) {
    Log.d(TAG, "ReplayChessBoard() is called")
    Log.d(TAG, "currIndex received as: $currIndex")

    val textMeasurer = rememberTextMeasurer()

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
        ) {
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

                            drawRect(
                                color = if (isDarkSquare)
                                    getColor(context, R.color.dark_square)
                                else
                                    getColor(context, R.color.light_square),
                                topLeft = Offset(j * squareWidth, i * squareHeight),
                                size = Size(squareWidth, squareHeight)
                            )
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
                        if (
                            row == 1 &&
                            currIndex != -1 &&
                            movesStringList[currIndex].length == 6 &&
                            squareKey == "${movesStringList[currIndex][3]}${movesStringList[currIndex][4]}"
                        ) {
                            if (movesStringList[currIndex].last() == 'q') {
                                map[Piece.BLACK_QUEEN]?.let { bitmap ->
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
                            else if (movesStringList[currIndex].last() == 'n') {
                                map[Piece.BLACK_KNIGHT]?.let { bitmap ->
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
                            else if (movesStringList[currIndex].last() == 'r') {
                                map[Piece.BLACK_ROOK]?.let { bitmap ->
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
                            else {
                                map[Piece.BLACK_BISHOP]?.let { bitmap ->
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
                        }
                        else if (
                            row == 8 &&
                            currIndex != -1 &&
                            movesStringList[currIndex].length == 6 &&
                            squareKey == "${movesStringList[currIndex][3]}${movesStringList[currIndex][4]}"
                        ) {
                            if (movesStringList[currIndex].last() == 'q') {
                                map[Piece.WHITE_QUEEN]?.let { bitmap ->
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
                            else if (movesStringList[currIndex].last() == 'n') {
                                map[Piece.WHITE_KNIGHT]?.let { bitmap ->
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
                            else if (movesStringList[currIndex].last() == 'r') {
                                map[Piece.WHITE_ROOK]?.let { bitmap ->
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
                            else {
                                map[Piece.WHITE_BISHOP]?.let { bitmap ->
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
                        }
                        else {
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
                        }

                        col++
                    }
                    row--
                    col = 0
                }
            }
            else {
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
                            drawRect(
                                color = if (isDarkSquare)
                                    getColor(context, R.color.dark_square)
                                else
                                    getColor(context, R.color.light_square),
                                topLeft = Offset(j * squareWidth, i * squareHeight),
                                size = Size(squareWidth, squareHeight)
                            )
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
                        if (
                            row == 1 &&
                            currIndex != -1 &&
                            movesStringList[currIndex].length == 6 &&
                            squareKey == "${movesStringList[currIndex][3]}${movesStringList[currIndex][4]}"
                        ) {
                            if (movesStringList[currIndex].last() == 'q') {
                                map[Piece.BLACK_QUEEN]?.let { bitmap ->
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
                            } else if (movesStringList[currIndex].last() == 'n') {
                                map[Piece.BLACK_KNIGHT]?.let { bitmap ->
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
                            } else if (movesStringList[currIndex].last() == 'r') {
                                map[Piece.BLACK_ROOK]?.let { bitmap ->
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
                            } else {
                                map[Piece.BLACK_BISHOP]?.let { bitmap ->
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
                        }
                        else if (
                            row == 8 &&
                            currIndex != -1 &&
                            movesStringList[currIndex].length == 6 &&
                            squareKey == "${movesStringList[currIndex][3]}${movesStringList[currIndex][4]}"
                        ) {
                            if (movesStringList[currIndex].last() == 'q') {
                                map[Piece.WHITE_QUEEN]?.let { bitmap ->
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
                            else if (movesStringList[currIndex].last() == 'n') {
                                map[Piece.WHITE_KNIGHT]?.let { bitmap ->
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
                            else if (movesStringList[currIndex].last() == 'r') {
                                map[Piece.WHITE_ROOK]?.let { bitmap ->
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
                            else {
                                map[Piece.WHITE_BISHOP]?.let { bitmap ->
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
                        }
                        else {
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
                        }
                        col--
                    }
                    col = 7
                    row++
                }
            }
        }
    }
}

@Preview
@Composable
private fun ReplayPreview() {
    val deviceBoard by remember { mutableStateOf(Board()) }
    val moves by remember { mutableStateOf<List<Move>>(emptyList()) }
    val currIndex by remember { mutableIntStateOf(0) }

    ReplayChessBoard(
        context = ReplayGameActivity(),
        color = Side.WHITE,
        deviceBoard = deviceBoard,
        movesStringList = listOf(),
        currIndex = currIndex
    )
}