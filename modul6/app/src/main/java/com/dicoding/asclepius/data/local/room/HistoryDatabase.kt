package com.dicoding.asclepius.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.local.entity.ResultEntity
import com.dicoding.asclepius.helper.Constants.HISTORY_DATABASE

@Database(entities = [ResultEntity::class], version = 1)
abstract class HistoryDatabase: RoomDatabase() {
    abstract fun historyDAO(): HistoryDAO

    companion object {
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): HistoryDatabase{
            if (INSTANCE == null){
                synchronized(HistoryDatabase::class.java){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        HistoryDatabase::class.java, HISTORY_DATABASE)
                        .build()
                }
            }

            return INSTANCE as HistoryDatabase
        }
    }
}