package com.singhDevs.chezz.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("/v1/auth/google")
    suspend fun authenticateWithGoogle(
        @Body request: GoogleAuthRequest
    ): Response<AuthResponse>

    @POST("/v1/game/join")
    suspend fun joinGame(
        @Header("Authorization") token: String,
        @Body request: JoinGameRequest
    ): Response<JoinGameResponse>
}

data class GoogleAuthRequest(val idToken: String)
data class AuthResponse(val token: String, val user: User)
@Parcelize
data class User(val id: String, val email: String, val username: String): Parcelable

data class JoinGameRequest(val userId: String)
data class JoinGameResponse(val gameId: String, val wsURL: String)
