package com.dicoding.asclepius.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dicoding.asclepius.helper.Constants.HISTORY_TABLE
import kotlinx.parcelize.Parcelize

@Entity(tableName = HISTORY_TABLE)
@Parcelize
class ResultEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "image")
    var image: String? = null,

    @ColumnInfo(name = "result")
    var result: String? = null,

    @ColumnInfo(name = "score")
    var score: String? = null,

    @ColumnInfo(name = "timeStamp")
    var timeStamp: String? = null
): Parcelable