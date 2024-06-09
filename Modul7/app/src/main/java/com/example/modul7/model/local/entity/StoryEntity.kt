package com.example.modul7.model.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.modul7.utils.Constants.STORY_TABLE
import kotlinx.parcelize.Parcelize

@Entity(tableName = STORY_TABLE)
@Parcelize
class StoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var storyId: String,

    @ColumnInfo(name = "photoUrl")
    var image: String? = null,

    @ColumnInfo(name = "name")
    var userName: String? = null,

    @ColumnInfo(name = "description")
    var desc: String? = null
): Parcelable