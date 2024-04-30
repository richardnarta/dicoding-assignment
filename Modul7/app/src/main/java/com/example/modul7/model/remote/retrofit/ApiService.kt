package com.example.modul7.model.remote.retrofit

import com.example.modul7.model.remote.response.DetailResponse
import com.example.modul7.model.remote.response.LoginResponse
import com.example.modul7.model.remote.response.RegisterResponse
import com.example.modul7.model.remote.response.StoryResponse
import com.example.modul7.model.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun createUser (
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun loginToAccount (
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    fun getStoryLists (): Call<StoryResponse>

    @GET("stories/{id}")
    fun getStoryDetail (
        @Path("id") id: String
    ): Call<DetailResponse>

    @Multipart
    @POST("stories")
    fun uploadStory (
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<UploadResponse>
}