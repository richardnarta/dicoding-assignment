package com.example.modul3_2.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.modul3_2.data.local.entity.UserEntity
import com.example.modul3_2.utils.Constants.BOOKMARK_TABLE

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: UserEntity)

    @Query("DELETE FROM $BOOKMARK_TABLE WHERE username=:username")
    fun delete(username: String)

    @Query("SELECT * from $BOOKMARK_TABLE ORDER BY id ASC")
    fun getAllUsers(): LiveData<List<UserEntity>>

    @Query("SELECT EXISTS(SELECT * FROM $BOOKMARK_TABLE WHERE username = :username)")
    suspend fun isUserBookmarked(username: String): Int
}