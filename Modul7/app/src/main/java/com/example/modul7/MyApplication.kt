package com.example.modul7

import android.app.Application
import com.google.android.material.color.DynamicColors

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}