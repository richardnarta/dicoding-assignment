package com.dicoding.asclepius.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.local.entity.ResultEntity
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.data.remote.response.Response
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import com.dicoding.asclepius.data.repository.HistoryRepository
import com.dicoding.asclepius.helper.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback

class ResultViewmodel (application: Application): ViewModel() {
    private val historyRepo: HistoryRepository = HistoryRepository(application)

    private val _news = MutableLiveData<List<ArticlesItem>?>()
    val news: LiveData<List<ArticlesItem>?> = _news

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = _snackBarText

    init {
        loadNews()
    }

    fun insert (result : ResultEntity) = historyRepo.insert(result)

    fun delete (timeStamp: String) = historyRepo.delete(timeStamp)

    fun checkHistory (timeStamp: String, callback: (Int)->Unit){
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                historyRepo.isUserAdded(timeStamp)
            }
            callback(result)
        }
    }

    private fun loadNews(){
        _isLoading.value = true

        val client = ApiConfig.getApiService().getNews()
        client.enqueue(object : Callback<Response>{
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                if (response.isSuccessful){
                    _isLoading.value = false
                    _news.value = response.body()?.articles
                }else {
                    _snackBarText.value = Event(response.message())
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                _isLoading.value = false
                _snackBarText.value = Event("${t.message}")
            }
        })
    }
}