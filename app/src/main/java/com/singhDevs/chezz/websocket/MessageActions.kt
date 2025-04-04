package com.singhDevs.chezz.websocket

import com.singhDevs.chezz.models.GameOverResponse
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.models.Move
import com.singhDevs.chezz.models.ResultType
import com.singhDevs.chezz.models.User
import com.singhDevs.chezz.models.chessboard.Board

interface MessageActions {
    fun onInfoReceived(onlineUsers: Int)
    fun onGameStart(color: Char, opponent: User, duration: Int, gameType: GameType)
    fun onMoveMade(board: Board, move: Move, piece: Char, whiteTime: Long, blackTime: Long)
    fun onGameOver(gameOverResponse: GameOverResponse)
    fun onDrawRequested()
}