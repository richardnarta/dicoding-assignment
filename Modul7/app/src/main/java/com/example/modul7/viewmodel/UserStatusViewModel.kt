package com.example.modul7.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.modul7.model.remote.UserRepository
import com.example.modul7.utils.Event

class UserStatusViewModel (private val userRepository: UserRepository): ViewModel() {
    var snackBarText: MutableLiveData<Event<String>> = MutableLiveData()

    fun loginToAccount (email: String, password: String) = userRepository.setLoginSession(email, password)

    fun registerAccount (name: String, email: String, password: String) = userRepository.registerAccount(name, email, password)
}