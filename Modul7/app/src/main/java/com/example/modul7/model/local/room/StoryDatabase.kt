package com.example.modul7.model.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.modul7.model.local.entity.RemoteKeys
import com.example.modul7.model.local.entity.StoryEntity
import com.example.modul7.model.local.room_key.RemoteKeysDAO
import com.example.modul7.utils.Constants.STORY_DATABASE

@Database(entities = [StoryEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false)
abstract class StoryDatabase: RoomDatabase() {

    abstract fun storyDao(): StoryDAO
    abstract fun remoteKeysDao(): RemoteKeysDAO

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase{
            if (INSTANCE == null) {
                synchronized(StoryDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        StoryDatabase::class.java, STORY_DATABASE).build()
                }
            }

            return INSTANCE as StoryDatabase
        }
    }
}