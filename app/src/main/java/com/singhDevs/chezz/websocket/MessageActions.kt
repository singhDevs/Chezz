package com.singhDevs.chezz.websocket

import com.singhDevs.chezz.models.Move
import com.singhDevs.chezz.models.chessboard.Board

interface MessageActions {
    fun onInfoReceived(onlineUsers: Int)
    fun onGameStart(color: Char, opponent: String)
    fun onMoveMade(board: Board, move: Move, piece: Char,result: Char? = null, cause: String? = null, whiteTime: Long, blackTime: Long)
    fun onGameOver(result: Char?, cause: String?, move: Move)
    fun onDrawRequested()
}