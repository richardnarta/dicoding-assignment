package com.example.modul3.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.modul3.data.response.UserResponse
import com.example.modul3.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowerViewModel: ViewModel() {
    private val _user = MutableLiveData<List<UserResponse>?>()
    val user: LiveData<List<UserResponse>?> = _user

    private val userName = MutableLiveData<String>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadFollower(){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFollower(userName.value!!)
        client.enqueue(object : Callback<List<UserResponse>> {
            override fun onResponse(call: Call<List<UserResponse>>, response: Response<List<UserResponse>>) {
                if(response.isSuccessful){
                    _isLoading.value = false
                    _user.value = response.body()
                }else{
                    Log.e("error", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<UserResponse>>, t: Throwable) {
                _isLoading.value = false
                Log.e("error", "onFailure: ${t.message}")
            }
        })
    }

    fun setUserName(username: String){
        userName.value = username
    }
}