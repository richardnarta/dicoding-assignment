package com.example.modul3_2.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modul3_2.data.local.entity.UserEntity
import com.example.modul3_2.data.remote.response.UserDetail
import com.example.modul3_2.data.remote.retrofit.ApiConfig
import com.example.modul3_2.data.repository.UserRepository
import com.example.modul3_2.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel (application: Application): ViewModel() {
    private val userRepo: UserRepository = UserRepository(application)

    private val _user = MutableLiveData<UserDetail?>()
    val user: LiveData<UserDetail?> = _user

    private val _userName = MutableLiveData<String>()
    private val userName: LiveData<String> = _userName

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = _snackBarText

    fun insert(user: UserEntity){
        userRepo.insert(user)
    }

    fun delete(user: String){
        userRepo.delete(user)
    }

    fun checkUser(user: String, callback: (Int)->Unit){
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                userRepo.isUserBookmarked(user)
            }
            callback(result)
        }
    }

    fun loadUserDetail(){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUserDetail(userName.value!!)
        client.enqueue(object : Callback<UserDetail> {
            override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                if(response.isSuccessful){
                    _isLoading.value = false
                    _user.value = response.body()
                }else{
                    _snackBarText.value = Event(response.message())
                }
            }

            override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                _isLoading.value = false
                _snackBarText.value = Event("${t.message}")
            }
        })
    }

    fun setUserName(username: String){
        _userName.value = username
    }
}