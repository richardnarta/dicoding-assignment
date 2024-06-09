package com.example.modul7.model.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.modul7.model.local.entity.StoryEntity
import com.example.modul7.utils.Constants.STORY_TABLE

@Dao
interface StoryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<StoryEntity>)

    @Query("SELECT * FROM $STORY_TABLE")
    fun getAllStory(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM $STORY_TABLE")
    suspend fun deleteAll()
}