package com.singhDevs.chezz.websocket

import android.util.Log
import android.widget.Toast
import com.singhDevs.chezz.utils.Constants
import com.singhDevs.chezz.models.Message
import com.singhDevs.chezz.models.MessageTypes
import com.singhDevs.chezz.models.chessboard.Board
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

private const val TAG = "WebSocketClient"

class WebSocketClient(val messageActions: MessageActions) {
    private val client = OkHttpClient()
    var webSocket: WebSocket? = null
    val gson = Gson()

    fun start(wsUrl: String, token: String) {
        Log.d(TAG, "start() method of  WebSocketClient called, with following params:")
        Log.d(TAG, "wsUrl: $wsUrl")
        Log.d(TAG, "token: $token")

        val request = Request.Builder()
            .url(wsUrl)
            .addHeader("Authorization", "Bearer $token")
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket opened")
                val initMessage = Gson().toJson(Message(type = MessageTypes.INIT_GAME.value))
                Constants.webSocketClient.webSocket?.send(initMessage)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Received message: $text")
                var message = gson.fromJson(text, Message::class.java)
                when (message.type) {
                    MessageTypes.INFO.value -> {
                        message.onlineUsers?.let {
                            Log.d(TAG, "Online Users: $it")
                            messageActions.onInfoReceived(it) }
                    }

                    MessageTypes.START_GAME.value -> {
                        messageActions.onGameStart(message.color!!, message.opponent!!)
                        return
                    }

                    MessageTypes.MOVE.value -> {
                        if (message.board is String) {
                            Log.d(TAG, "Board is String, deserializing...")
                            val boardJson = message.board as String
                            val board = gson.fromJson(boardJson, Board::class.java)
                            message = message.copy(board = board)

                            Log.d(TAG, "Received Board:-")
                            Log.d(TAG, board.toString())

                            Log.d(TAG, "Sending updated board to ChessBoard Screen...")
                            message.move?.let {
                                messageActions.onMoveMade(
                                    board,
                                    it,
                                    message.piece!![0],
                                    result = message.result,
                                    message.cause,
                                    message.whiteTime!!,
                                    message.blackTime!!
                                )
                            }
                        }

                        Log.d(TAG, "type: ${message.type}")
                        if (message.color != null) Log.d(TAG, "color: ${message.color}")
                        if (message.board != null) Log.d(TAG, "board: ${message.board}")
                        if (message.move != null) {
                            Log.d(
                                TAG,
                                "move - from: ${message.move?.from}\tto: ${message.move?.to}"
                            )

                        }
                        if (message.result != null) Log.d(TAG, "result: ${message.result}")
                    }

                    MessageTypes.GAME_OVER.value -> {
                        message.move?.let {
                            messageActions.onGameOver(
                                message.result,
                                message.cause,
                                it
                            )
                        }
                    }

                    MessageTypes.DRAW_REQUESTED.value -> {
                        messageActions.onDrawRequested()
                    }
                }

                println("Received message: $text")
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d(TAG, "Received bytes: $bytes")
                println("Received bytes: $bytes")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket error: $t\tResponse: $response")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code / $reason")
                webSocket.close(code, reason)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $code / $reason")
            }
        }

        webSocket = client.newWebSocket(request, listener)
    }
}