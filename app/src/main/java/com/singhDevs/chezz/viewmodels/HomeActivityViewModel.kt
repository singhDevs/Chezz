package com.singhDevs.chezz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.singhDevs.chezz.UserRatingsOuterClass
import com.singhDevs.chezz.data.RatingsRepository
import com.singhDevs.chezz.models.Ratings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeActivityViewModel(private val ratingsRepository: RatingsRepository): ViewModel() {
    var ratings = Ratings()

    val ratingsFlow: StateFlow<UserRatingsOuterClass.UserRatings> = ratingsRepository.ratingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UserRatingsOuterClass.UserRatings.getDefaultInstance()
        )

    suspend fun clearData() = ratingsRepository.clearData()

    fun setRatings(ratings: UserRatingsOuterClass.UserRatings){
        this.ratings = Ratings(ratings.bulletRating, ratings.blitzRating, ratings.rapidRating)
    }
}

class HomeViewModelFactory(
    private val ratingsRepository: RatingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeActivityViewModel(ratingsRepository) as T
    }
}