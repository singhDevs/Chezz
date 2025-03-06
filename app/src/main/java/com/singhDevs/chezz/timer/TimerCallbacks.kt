package com.singhDevs.chezz.timer

interface TimerCallbacks {
    fun onTimeUpdate(time: Long)
    fun onServerTimeSync(whiteTime: Long, blackTime: Long)
}