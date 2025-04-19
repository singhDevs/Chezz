package com.singhDevs.chezz.network

import com.google.gson.GsonBuilder
import com.singhDevs.chezz.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = BuildConfig.baseURL
    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val authServiceInstance: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val gameServiceInstance: GameService by lazy {
        retrofit.create(GameService::class.java)
    }

    val userServiceInstance: UserService by lazy {
        retrofit.create(UserService::class.java)
    }
}