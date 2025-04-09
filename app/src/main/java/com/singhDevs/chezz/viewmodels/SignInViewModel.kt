package com.singhDevs.chezz.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.singhDevs.chezz.UserRatingsOuterClass
import com.singhDevs.chezz.data.RatingsRepository
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.models.Ratings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "SignInViewModel"
class SignInViewModel(private val ratingsRepository: RatingsRepository): ViewModel() {
    fun saveAllRatings(ratings: Ratings) {
        viewModelScope.launch {
            try {
                ratingsRepository.updateAllRatings(ratings.bulletRating, ratings.blitzRating, ratings.rapidRating)
                Log.d(TAG, "All ratings saved successfully")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to save ratings", e)
            }
        }
    }

    val ratings: StateFlow<UserRatingsOuterClass.UserRatings> = ratingsRepository.ratingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UserRatingsOuterClass.UserRatings.getDefaultInstance()
        )
}

class SignInViewModelFactory(
    private val ratingsRepository: RatingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignInViewModel(ratingsRepository) as T
    }
}