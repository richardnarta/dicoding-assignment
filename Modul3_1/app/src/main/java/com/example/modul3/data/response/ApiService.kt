package com.example.modul3.data.response

import com.example.modul3.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    @GET("search/users")
    fun getUser(
        @Query("q") q:String
    ): Call<ResponseApi>

    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    @GET("users/{login}")
    fun getUserDetail(
        @Path("login") login:String
    ): Call<UserDetail>

    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    @GET("users/{login}/followers")
    fun getFollower(
        @Path("login") login:String
    ): Call<List<UserResponse>>

    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    @GET("users/{login}/following")
    fun getFollowing(
        @Path("login") login:String
    ): Call<List<UserResponse>>
}