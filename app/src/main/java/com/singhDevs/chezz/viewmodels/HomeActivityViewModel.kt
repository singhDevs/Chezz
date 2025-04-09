package com.singhDevs.chezz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.singhDevs.chezz.UserRatingsOuterClass
import com.singhDevs.chezz.data.RatingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeActivityViewModel(private val ratingsRepository: RatingsRepository): ViewModel() {
    val ratings: StateFlow<UserRatingsOuterClass.UserRatings> = ratingsRepository.ratingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UserRatingsOuterClass.UserRatings.getDefaultInstance()
        )

    suspend fun clearData() = ratingsRepository.clearData()
}

class HomeViewModelFactory(
    private val ratingsRepository: RatingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeActivityViewModel(ratingsRepository) as T
    }
}