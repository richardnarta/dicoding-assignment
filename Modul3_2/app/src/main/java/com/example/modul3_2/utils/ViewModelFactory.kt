package com.example.modul3_2.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.modul3_2.data.local.datastore.SettingPreferences
import com.example.modul3_2.ui.viewmodel.DetailViewModel
import com.example.modul3_2.ui.viewmodel.FavoriteViewModel
import com.example.modul3_2.ui.viewmodel.SettingViewModel

class ViewModelFactory private constructor(private val application: Application): ViewModelProvider.NewInstanceFactory() {
    companion object{
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance (application: Application): ViewModelFactory{
            if (INSTANCE == null){
                synchronized(ViewModelFactory::class.java){
                    INSTANCE = ViewModelFactory(application)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T{
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)){
            return DetailViewModel(application) as T
        }else if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)){
            return FavoriteViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class SettingViewModelFactory (private val pref: SettingPreferences): ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T{
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)){
            return SettingViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}