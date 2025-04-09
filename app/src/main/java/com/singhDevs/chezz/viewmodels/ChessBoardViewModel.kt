package com.singhDevs.chezz.viewmodels

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.singhDevs.chezz.UserRatingsOuterClass
import com.singhDevs.chezz.UserRatingsOuterClass.UserRatings
import com.singhDevs.chezz.data.RatingsRepository
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.timer.ChessTimer
import com.singhDevs.chezz.timer.TimerCallbacks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "ChessBoardViewModel"

class ChessBoardViewModel (private val ratingsRepository: RatingsRepository) : ViewModel(), TimerCallbacks {
    private var gameTime: Long = 0

    private var initialRatings = MutableStateFlow<UserRatings?>(null)
    val _initialRatings: StateFlow<UserRatings?> = initialRatings.asStateFlow()

    lateinit var _whiteTimer: ChessTimer
    lateinit var _blackTimer: ChessTimer

    lateinit var whiteCurrentTime: MutableLiveData<Long>
    lateinit var blackCurrentTime: MutableLiveData<Long>

    suspend fun initialize(gameTime: Long){
        this.gameTime = gameTime
        _whiteTimer = ChessTimer(gameTime, this)
        _blackTimer = ChessTimer(gameTime, this)
        whiteCurrentTime = MutableLiveData(gameTime)
        blackCurrentTime = MutableLiveData(gameTime)
        initialRatings.value = ratingsRepository.getCurrentRatings()
    }

    fun handleTimers() {
        if (_whiteTimer.isRunning) {
            Log.d(TAG, "handleTimers: pausing WHITE, resuming BLACK...")
            _whiteTimer.pause()
            _blackTimer.start()
        } else if (_blackTimer.isRunning) {
            Log.d(TAG, "handleTimers: pausing BLACK, resuming WHITE...")
            _blackTimer.pause()
            _whiteTimer.start()
        }
    }

    fun stopTimers() {
        _whiteTimer.stop()
        _blackTimer.stop()
    }

    override fun onTimeUpdate(time: Long) {
        if (_whiteTimer.isRunning)
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

    fun updateRating(gameType: GameType, newValue: Int) {
        viewModelScope.launch {
            ratingsRepository.updateRatings(gameType, newValue)
        }
    }

    val ratings: StateFlow<UserRatingsOuterClass.UserRatings> = ratingsRepository.ratingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UserRatingsOuterClass.UserRatings.getDefaultInstance()
        )
}

class ChessViewModelFactory(
    private val repo: RatingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChessBoardViewModel(repo) as T
    }
}