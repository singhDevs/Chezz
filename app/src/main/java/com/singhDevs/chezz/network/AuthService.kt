package com.singhDevs.chezz.network

import android.os.Parcelable
import com.singhDevs.chezz.models.Game
import com.singhDevs.chezz.models.Ratings
import kotlinx.parcelize.Parcelize
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.Date

interface AuthService {
    @POST("/v1/auth/google")
    suspend fun authenticateWithGoogle(
        @Body request: GoogleAuthRequest
    ): Response<AuthResponse>
}

data class GoogleAuthRequest(val idToken: String)
data class AuthResponse(val token: String, val message: String, val user: User)

@Parcelize
data class User(
    val id: String,
    val email: String,
    val username: String,
    val photoUrl: String?,
    val createdAt: Date,
    val totalGames: Int = 0,
    val totalWins: Int = 0,
    val totalLosses: Int = 0,
    val totalDraws: Int = 0,
    val totalTimePlayed: Int = 0,
    val ratings: Ratings = Ratings(),
) : Parcelable