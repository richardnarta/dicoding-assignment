package com.example.modul7.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.modul7.model.remote.UserRepository
import com.example.modul7.di.Injection
import com.example.modul7.model.remote.StoryRepository

class UserViewModelFactory private constructor(private val userRepository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserStatusViewModel::class.java)) {
            return UserStatusViewModel(userRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {

        @Volatile
        private var instance: UserViewModelFactory? = null
        fun getInstance (context: Context): UserViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: UserViewModelFactory(Injection.provideUserRepository(context))
            }.also { instance = it }
    }
}

class StoryViewModelFactory private constructor(private val storyRepository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(storyRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {

        @Volatile
        private var instance: StoryViewModelFactory? = null
        fun getInstance (context: Context): StoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: StoryViewModelFactory(Injection.provideStoryRepository(context))
            }.also { instance = it }
    }
}