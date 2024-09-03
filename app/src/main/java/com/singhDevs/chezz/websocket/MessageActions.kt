package com.singhDevs.chezz.websocket

import com.singhDevs.chezz.models.Move
import com.singhDevs.chezz.models.chessboard.Board

interface MessageActions {
    fun onGameStart(color: Char)
    fun onOpponentMoveMade(board: Board, move: Move)
    fun onMoveMade(board: Board, move: com.github.bhlangonijr.chesslib.move.Move)
}