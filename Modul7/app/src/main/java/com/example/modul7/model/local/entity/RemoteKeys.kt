package com.example.modul7.model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.modul7.utils.Constants.KEY_TABLE

@Entity(tableName = KEY_TABLE)
data class RemoteKeys(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)
