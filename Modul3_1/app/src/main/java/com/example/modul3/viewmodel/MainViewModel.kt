package com.example.modul3.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.modul3.data.response.ResponseApi
import com.example.modul3.data.response.UserResponse
import com.example.modul3.data.retrofit.ApiConfig
import com.example.modul3.util.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {

    private val _user = MutableLiveData<List<UserResponse>?>()
    val user: LiveData<List<UserResponse>?> = _user

    private val query = MutableLiveData("richardnarta")

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = _snackBarText

    init {
        loadUser()
    }

    fun loadUser(){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUser(query.value!!)
        client.enqueue(object : Callback<ResponseApi>{
            override fun onResponse(call: Call<ResponseApi>, response: Response<ResponseApi>) {
                if(response.isSuccessful){
                    _isLoading.value = false
                    _user.value = response.body()?.items
                }else{
                    _snackBarText.value = Event(response.message())
                }
            }

            override fun onFailure(call: Call<ResponseApi>, t: Throwable) {
                _isLoading.value = false
                _snackBarText.value = Event("${t.message}")
            }
        })
    }

    fun setQuery(q: String){
        query.value = q
    }
}