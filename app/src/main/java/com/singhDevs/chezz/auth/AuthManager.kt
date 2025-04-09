package com.singhDevs.chezz.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.singhDevs.chezz.network.User
import androidx.core.content.edit
import java.util.Date

class AuthManager private constructor(context: Context) {
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "auth_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveUserData(token: String, user: User) {
        sharedPreferences.edit {
            putString("auth_token", token)
                .putString("user_id", user.id)
                .putString("user_email", user.email)
                .putString("user_name", user.username)
                .putString("photoUrl", user.photoUrl)
                .putLong("created_at_millis", user.createdAt.time)
                .putInt("total_games", user.totalGames)
                .putInt("total_wins", user.totalWins)
                .putInt("total_losses", user.totalLosses)
                .putInt("total_draws", user.totalDraws)
                .putInt("total_time_played", user.totalTimePlayed)
        }
    }

    fun getAuthToken(): String? = sharedPreferences.getString("auth_token", null)

    fun getUser(): User? {
        val id = sharedPreferences.getString("user_id", null)
        val email = sharedPreferences.getString("user_email", null)
        val userName = sharedPreferences.getString("user_name", null)
        val photoUrl = sharedPreferences.getString("photoUrl", null)
        val createdMillis = sharedPreferences.getLong("created_at_millis", -1L)
        val createdAt = Date(createdMillis)

        val totalGames = sharedPreferences.getInt("total_games", 0)
        val totalWins = sharedPreferences.getInt("total_wins", 0)
        val totalLosses = sharedPreferences.getInt("total_losses", 0)
        val totalDraws = sharedPreferences.getInt("total_draws", 0)
        val totalTimePlayed = sharedPreferences.getInt("total_time_played", 0)

        return if (id != null && email != null && userName != null && photoUrl != null) User(
            id,
            email,
            userName,
            photoUrl,
            createdAt,
            totalGames,
            totalWins,
            totalLosses,
            totalDraws,
            totalTimePlayed
        ) else null
    }

    fun isLoggedIn(): Boolean = getAuthToken() != null

    fun clearCredentials() {
        sharedPreferences.edit { clear() }
    }

    companion object {
        @Volatile
        private var instance: AuthManager? = null
        fun getInstance(context: Context): AuthManager =
            instance ?: synchronized(this) {
                instance ?: AuthManager(context.applicationContext).also { instance = it }
            }
    }
}