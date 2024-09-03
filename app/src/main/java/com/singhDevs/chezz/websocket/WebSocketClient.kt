package com.singhDevs.chezz.websocket

import android.util.Log
import com.singhDevs.chezz.Constants
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
    companion object{
        var message: Message? = null
    }

    fun start(){
        val request = Request.Builder()
            .url("ws://10.0.2.2:8080")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener(){
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket opened")
                val initMessage = Gson().toJson(Message(type = MessageTypes.INIT_GAME.value))
                Constants.webSocketClient.webSocket?.send(initMessage)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Received message: $text")

                var message = gson.fromJson(text, Message::class.java)
                if(message.type == MessageTypes.INIT_GAME.value){
                    messageActions.onGameStart(message.color!![0])
                    return
                }
                else{
                    if (message.board is String) {
                        Log.d(TAG, "Board is String, deserializing...")
                        val boardJson = message.board as String
                        val board = gson.fromJson(boardJson, Board::class.java)
                        message = message.copy(board = board)

                        Log.d(TAG, "Received Board:-")
                        Log.d(TAG, board.toString())

                        Log.d(TAG, "Sending updated board to ChessBoard Screen...")
                        message.move?.let { messageActions.onOpponentMoveMade(board, it) }
                    }

                    Log.d(TAG, "type: ${message.type}")
                    if(message.color != null) Log.d(TAG, "color: ${message.color}")
                    if(message.board != null) Log.d(TAG, "board: ${message.board}")
                    if(message.move != null) {
                        Log.d(TAG, "move - from: ${message.move?.from}\tto: ${message.move?.to}")

                    }
                    if(message.result != null) Log.d(TAG, "result: ${message.result}")
                }

                println("Received message: $text")
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d(TAG, "Received bytes: $bytes")
                println("Received bytes: $bytes")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket error: ${t.message}")
                println("WebSocket error: ${t.message}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code / $reason")
                println("WebSocket closing: $code / $reason")
                webSocket.close(code, reason)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $code / $reason")
                println("WebSocket closed: $code / $reason")
            }
        })
    }
}