package com.example.modul7.model.local.room_key

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.modul7.model.local.entity.RemoteKeys
import com.example.modul7.utils.Constants.KEY_TABLE

@Dao
interface RemoteKeysDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM $KEY_TABLE WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): RemoteKeys?

    @Query("DELETE FROM $KEY_TABLE")
    suspend fun deleteRemoteKeys()
}