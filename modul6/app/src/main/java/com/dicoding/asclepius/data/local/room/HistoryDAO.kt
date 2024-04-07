package com.dicoding.asclepius.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.asclepius.data.local.entity.ResultEntity
import com.dicoding.asclepius.helper.Constants.HISTORY_TABLE

@Dao
interface HistoryDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(result: ResultEntity)

    @Query("DELETE FROM $HISTORY_TABLE WHERE timeStamp=:timeStamp")
    fun delete(timeStamp: String)

    @Query("SELECT * from $HISTORY_TABLE ORDER BY id ASC")
    fun getAllHistory(): LiveData<List<ResultEntity>>

    @Query("SELECT EXISTS(SELECT * FROM $HISTORY_TABLE WHERE timeStamp=:timeStamp)")
    suspend fun isHistoryAdded(timeStamp: String): Int
}