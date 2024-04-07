package com.dicoding.asclepius.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.local.entity.ResultEntity
import com.dicoding.asclepius.data.repository.HistoryRepository

class HistoryViewmodel (application: Application): ViewModel() {
    private val historyRepo: HistoryRepository = HistoryRepository(application)

    fun getAllHistory(): LiveData<List<ResultEntity>> = historyRepo.getAllHistory()
}