package com.singhDevs.chezz

import android.os.Bundle
import android.util.Log
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
import com.singhDevs.chezz.ComposeUtils.CircularProgress
import com.singhDevs.chezz.Constants.squareMapping
import com.singhDevs.chezz.Constants.squareToCharMapping
import com.singhDevs.chezz.models.Message
import com.singhDevs.chezz.models.MessageTypes
import com.singhDevs.chezz.models.Move
import com.singhDevs.chezz.models.chessboard.Board
import com.singhDevs.chezz.screens.ChessBoard
import com.singhDevs.chezz.ui.theme.ChessTheme
import com.singhDevs.chezz.websocket.MessageActions
import com.github.bhlangonijr.chesslib.Square
import com.google.gson.Gson
import com.github.bhlangonijr.chesslib.Board as DeviceBoard

private const val TAG = "WebSocketClient"


class GameActivity : ComponentActivity(), MessageActions {
    init {
        Constants.initClient(this)
        Constants.webSocketClient.start()
    }

    private var deviceBoard by mutableStateOf(DeviceBoard())
    private var board by mutableStateOf(Board())
    private var color by mutableStateOf('w')
    private var legalMoves by mutableStateOf<List<com.github.bhlangonijr.chesslib.move.Move>?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChessTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (color == 'a') {
                        CircularProgress(text = "Finding opponent...")
                    } else {
                        if (legalMoves == null) {
                            ChessBoard(
                                messageActions = this,
                                color = color,
                                context = this,
                                modifier = Modifier.padding(innerPadding),
                                board = board,
                                deviceBoard = deviceBoard,
                                legalMoves = emptyList()
                            )
                        } else {
                            ChessBoard(
                                messageActions = this,
                                color = color,
                                context = this,
                                modifier = Modifier.padding(innerPadding),
                                board = board,
                                deviceBoard = deviceBoard,
                                legalMoves = legalMoves!!
                            )
                        }

                    }
                }
            }
        }
    }

    override fun onGameStart(color: Char) {
        this.color = color
    }

    override fun onOpponentMoveMade(board: Board, move: Move) {
        Log.d(TAG, "onMoveMade: $board")
        deviceBoard.doMove(
            com.github.bhlangonijr.chesslib.move.Move(
                squareMapping[move.from],
                squareMapping[move.to]
            )
        )
        this.board = board
        this.legalMoves = deviceBoard.legalMoves()

        Log.d(TAG, "onMoveMade, printing deviceBoard:-\n$deviceBoard")
        Log.d(TAG, "Now printing Legal available moves:-")
        (this.legalMoves as MutableList<com.github.bhlangonijr.chesslib.move.Move>?)?.forEach { legalMove ->
            Log.d(TAG, legalMove.toString())
        }
    }

    override fun onMoveMade(board: Board, move: com.github.bhlangonijr.chesslib.move.Move) {
        Log.d(TAG, "onMoveMade: $board")
        val b7 = deviceBoard.getPiece(Square.B7)
        val b5 = deviceBoard.getPiece(Square.B5)
        val b7type = b7.pieceType
        val b5type = b5.pieceType

        if(b7 != null) Log.d(TAG, "Piece at Square B7: $b7")
        else Log.d(TAG, "Piece at Square B7: NULL")
        if(b7type != null) Log.d(TAG, "Piece type B7: $b7type")
        else Log.d(TAG, "Piece type B7: NULL")

        if(b5 != null) Log.d(TAG, "Piece at Square B5: $b5")
        else Log.d(TAG, "Piece at Square B5: NULL")
        if(b5type != null) Log.d(TAG, "Piece type B5: $b5")
        else Log.d(TAG, "Piece type B5: NULL")

        deviceBoard.doMove(
            com.github.bhlangonijr.chesslib.move.Move(
                Square.B7,
                Square.B5
            )
        )
        this.board = board
        this.legalMoves = deviceBoard.legalMoves()

        val moveMsg = Gson().toJson(
            Message(
                type = MessageTypes.MOVE.value,
                move = squareToCharMapping[move.from]?.let {
                    squareToCharMapping[move.to]?.let { it1 ->
                        Move(
                            from = it,
                            to = it1
                        )
                    }
                })
        )
        Log.d(TAG, "Move message ready to be sent: $moveMsg")
        Log.d(TAG, "Sending move to server...")
        Constants.webSocketClient.webSocket?.send(moveMsg)
    }

    override fun onDestroy() {
        super.onDestroy()
        Constants.webSocketClient.webSocket?.close(1000, "Activity destroyed")

    }
}