package com.example.modul3_2.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.modul3_2.data.local.entity.UserEntity
import com.example.modul3_2.data.repository.UserRepository

class FavoriteViewModel (application: Application): ViewModel() {
    private val userRepo: UserRepository = UserRepository(application)

    fun getAllFavoriteUser(): LiveData<List<UserEntity>> = userRepo.getAllUsers()
}