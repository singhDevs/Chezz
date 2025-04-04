package com.singhDevs.chezz.network

import com.singhDevs.chezz.models.Game
import com.singhDevs.chezz.models.GameMode
import com.singhDevs.chezz.models.GameType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GameService {
    @GET("v1/game/pgn")
    suspend fun getPGNData(
        @Header("Authorization") token: String,
        @Query("gameId") gameId: String
    ): Response<PGNData>

    @POST("/v1/game/join")
    suspend fun joinGame(
        @Header("Authorization") token: String,
        @Body request: JoinGameRequest
    ): Response<JoinGameResponse>

    @GET("v1/game/history")
    suspend fun getGames(
        @Header("Authorization") token: String
    ): Response<GetGameResponse>
}

data class PGNData(val pgn: String)
data class JoinGameRequest(val userId: String, val duration: Int, val gameMode: GameMode, val gameType: GameType)
data class JoinGameResponse(val gameId: String, val wsURL: String)
data class GetGameResponse(val games: List<Game>)