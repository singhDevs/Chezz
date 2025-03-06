package com.singhDevs.chezz.timer

import android.os.Handler
import android.os.Looper

class ChessTimer(
    gameTime: Long,
    private val callbacks: TimerCallbacks
) {
    var isRunning = false
    private var currentTime: Long = gameTime
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable{
        override fun run() {
            if(currentTime > 0) {
                currentTime -= 1000
                callbacks.onTimeUpdate(currentTime)
            }
            handler.postDelayed(this, 1000)
        }
    }

    fun syncTimeWithServer(time: Long){
        currentTime = time
    }

    fun start(){
        if(!isRunning){
            isRunning = true
            handler.postDelayed(runnable, 1000)
        }
    }

    fun pause(){
        if(isRunning){
            isRunning = false
            handler.removeCallbacks(runnable)
        }
    }

    fun stop(){
        pause()
    }
}