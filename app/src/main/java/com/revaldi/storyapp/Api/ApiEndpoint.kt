package com.revaldi.storyapp.Api

import com.revaldi.storyapp.Models.LoginData
import com.revaldi.storyapp.Models.UserData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiEndpoint {

    @POST("register")
    suspend fun registerUser(@Body request: UserData): UserRegistrationResponse

    @POST("login")
    suspend fun loginUser(@Body request: LoginData): UserLoginResponse


    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("location") location: Int?
    ): StoriesResponse



    @GET("stories/{id}")
    suspend fun getDetailStories(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): DetailStoriesResponse


    @Multipart
    @POST("/v1/stories")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ): Call<FileUploadResponse>



}