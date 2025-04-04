package com.singhDevs.chezz.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.singhDevs.chezz.network.User
import androidx.core.content.edit

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
        }
    }

    fun getAuthToken() : String? = sharedPreferences.getString("auth_token", null)

    fun getUser(): User? {
        val id = sharedPreferences.getString("user_id", null)
        val email = sharedPreferences.getString("user_email", null)
        val userName = sharedPreferences.getString("user_name", null)
        val photoUrl = sharedPreferences.getString("photoUrl", null)
        return if (id != null && email != null && userName != null && photoUrl != null) User(id, email, userName, photoUrl) else null
    }

    fun isLoggedIn(): Boolean = getAuthToken() != null

    fun clearCredentials() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        @Volatile private var instance: AuthManager? = null
        fun getInstance(context: Context): AuthManager =
            instance ?: synchronized(this) {
                instance ?: AuthManager(context.applicationContext).also { instance = it }
            }
    }
}