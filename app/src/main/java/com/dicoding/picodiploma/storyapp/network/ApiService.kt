package com.dicoding.picodiploma.storyapp.network

import com.dicoding.picodiploma.storyapp.network.FileUploadResponse
import com.dicoding.picodiploma.storyapp.network.StoryResponse
import com.dicoding.picodiploma.storyapp.ui.login.LoginResponse
import com.dicoding.picodiploma.storyapp.ui.register.SignupResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignupResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): Call<StoryResponse>

    @Multipart
    @POST("/v1/stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<FileUploadResponse>
}