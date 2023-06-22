package com.revaldi.storyapp.Api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BASE_URL = "https://story-api.dicoding.dev/v1/"

    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttp)
        .build()

    val instanceRetrofit: ApiEndpoint = retrofit.create(ApiEndpoint::class.java)



}