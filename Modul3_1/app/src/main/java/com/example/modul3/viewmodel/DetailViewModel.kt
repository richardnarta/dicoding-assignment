package com.example.modul3.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.modul3.data.response.UserDetail
import com.example.modul3.data.retrofit.ApiConfig
import com.example.modul3.util.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel: ViewModel() {
    private val _user = MutableLiveData<UserDetail?>()
    val user: LiveData<UserDetail?> = _user

    private val userName = MutableLiveData<String>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = _snackBarText

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
        userName.value = username
    }
}