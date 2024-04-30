package com.example.modul7.di

import android.content.Context
import com.example.modul7.model.local.datastore.LoginSession
import com.example.modul7.model.local.datastore.dataStore
import com.example.modul7.model.remote.StoryRepository
import com.example.modul7.model.remote.UserRepository
import com.example.modul7.model.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository (context: Context): UserRepository {
        val pref = LoginSession.getInstance(context.dataStore)

        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService, pref)
    }

    fun provideStoryRepository (context: Context): StoryRepository {
        val pref = LoginSession.getInstance(context.dataStore)
        val token = runBlocking { pref.getSessionToken().first() }
        val apiService = ApiConfig.getApiService(token)

        return  StoryRepository.getInstance(apiService)
    }
}