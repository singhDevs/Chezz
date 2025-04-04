package com.singhDevs.chezz.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import androidx.datastore.core.DataStore
import com.singhDevs.chezz.UserRatingsOuterClass.UserRatings
import kotlinx.coroutines.launch
import java.io.IOException

class RatingsViewModel(private val dataStore: DataStore<UserRatings>) : ViewModel() {

    val userRatings: StateFlow<UserRatings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(UserRatings.getDefaultInstance())
            } else {
                throw exception
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserRatings.getDefaultInstance()
        )

    fun updateBulletRating(newRating: Int) {
        updateRating { it.toBuilder().setBulletRating(newRating).build() }
    }

    fun updateBlitzRating(newRating: Int) {
        updateRating { it.toBuilder().setBlitzRating(newRating).build() }
    }

    fun updateRapidRating(newRating: Int) {
        updateRating { it.toBuilder().setRapidRating(newRating).build() }
    }

    private fun updateRating(transform: (UserRatings) -> UserRatings) {
        viewModelScope.launch {
            dataStore.updateData(transform)
        }
    }
}