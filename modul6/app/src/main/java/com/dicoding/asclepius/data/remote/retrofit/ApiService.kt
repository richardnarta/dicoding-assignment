package com.dicoding.asclepius.data.remote.retrofit

import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.remote.response.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    fun getNews(
        @Query("country") country: String = "us",
        @Query("category") category: String = "health",
        @Query("apiKey") key: String = BuildConfig.API_KEY
    ): Call<Response>
}