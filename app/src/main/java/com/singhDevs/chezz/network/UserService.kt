package com.singhDevs.chezz.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {
    @GET("v1/user/profile")
    suspend fun getUserProfile(
        @Query("userId") userId: String
    ): Response<GetUserProfileResponse>
}

data class GetUserProfileResponse(val user: User)