package com.singhDevs.chezz.data

import android.content.Context
import androidx.datastore.core.DataStore
import com.singhDevs.chezz.UserRatingsOuterClass.UserRatings
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.utils.userRatingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RatingsRepository(context: Context) {
    private val dataStore: DataStore<UserRatings> = context.userRatingsDataStore

    val ratingsFlow: Flow<UserRatings> = dataStore.data

    suspend fun clearData() = dataStore.updateData { it.toBuilder().clear().build() }

    suspend fun getCurrentRatings() = dataStore.data.first()

    suspend fun updateRatings(gameType: GameType, newValue: Int) {
        dataStore.updateData { current ->
            when (gameType) {
                GameType.BULLET -> current.toBuilder().setBulletRating(newValue).build()
                GameType.BLITZ -> current.toBuilder().setBlitzRating(newValue).build()
                GameType.RAPID -> current.toBuilder().setRapidRating(newValue).build()
            }
        }
    }

    suspend fun updateAllRatings(
        bullet: Int,
        blitz: Int,
        rapid: Int
    ) {
        dataStore.updateData { current ->
            current.toBuilder()
                .setBulletRating(bullet)
                .setBlitzRating(blitz)
                .setRapidRating(rapid)
                .build()
        }
    }
}