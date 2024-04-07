package com.dicoding.asclepius.data.repository

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.local.entity.ResultEntity
import com.dicoding.asclepius.data.local.room.HistoryDAO
import com.dicoding.asclepius.data.local.room.HistoryDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository (application: Application){
    private val historyDAO: HistoryDAO
    private val appExecutors: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HistoryDatabase.getDatabase(application)
        historyDAO = db.historyDAO()
    }

    fun getAllHistory(): LiveData<List<ResultEntity>> = historyDAO.getAllHistory()

    @WorkerThread
    suspend fun isUserAdded(timeStamp: String): Int = historyDAO.isHistoryAdded(timeStamp)

    fun insert(result: ResultEntity){
        appExecutors.execute { historyDAO.insert(result) }
    }

    fun delete(timeStamp: String){
        appExecutors.execute { historyDAO.delete(timeStamp) }
    }
}