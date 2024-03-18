package com.example.modul3_2.data.repository

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.modul3_2.data.local.entity.UserEntity
import com.example.modul3_2.data.local.room.UserDao
import com.example.modul3_2.data.local.room.UserDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserRepository (application: Application){
    private val userDao: UserDao
    private val appExecutors: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = UserDatabase.getDatabase(application)
        userDao = db.userDao()
    }

    fun getAllUsers(): LiveData<List<UserEntity>> = userDao.getAllUsers()

    @WorkerThread
    suspend fun isUserBookmarked(username: String): Int = userDao.isUserBookmarked(username)

    fun insert(user: UserEntity){
        appExecutors.execute { userDao.insert(user)}
    }

    fun delete(user: String){
        appExecutors.execute { userDao.delete(user) }
    }
}