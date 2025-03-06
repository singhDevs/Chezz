package com.singhDevs.chezz.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.singhDevs.chezz.timer.ChessTimer
import com.singhDevs.chezz.timer.TimerCallbacks

private const val TAG = "ChessBoardViewModel"

class ChessBoardViewModel(gameTime: Long): ViewModel(), TimerCallbacks {
     var _whiteTimer: ChessTimer = ChessTimer(gameTime, this)
     var _blackTimer: ChessTimer = ChessTimer(gameTime, this)
//    val whiteTimer: ChessTimer = _whiteTimer
//    val blackTimer: ChessTimer = _blackTimer

    val whiteCurrentTime: MutableLiveData<Long> = MutableLiveData(gameTime)
    val blackCurrentTime: MutableLiveData<Long> = MutableLiveData(gameTime)

    fun handleTimers(){
        if(_whiteTimer.isRunning){
            Log.d(TAG, "handleTimers: pausing WHITE, resuming BLACK...")
            _whiteTimer.pause()
            _blackTimer.start()
        }
        else if(_blackTimer.isRunning){
            Log.d(TAG, "handleTimers: pausing BLACK, resuming WHITE...")
            _blackTimer.pause()
            _whiteTimer.start()
        }
    }

    fun stopTimers(){
        _whiteTimer.stop()
        _blackTimer.stop()
    }

    override fun onTimeUpdate(time: Long) {
        if(_whiteTimer.isRunning)
            whiteCurrentTime.postValue(time)
        else
            blackCurrentTime.postValue(time)
    }

    override fun onServerTimeSync(whiteTime: Long, blackTime: Long) {
        whiteCurrentTime.postValue(whiteTime)
        blackCurrentTime.postValue(blackTime)
        _whiteTimer.syncTimeWithServer(whiteTime)
        _blackTimer.syncTimeWithServer(blackTime)
    }
}