package com.example.modul3_2.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.modul3_2.utils.Constants.BOOKMARK_TABLE
import kotlinx.parcelize.Parcelize

@Entity(tableName = BOOKMARK_TABLE)
@Parcelize
class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String? = null,

    @ColumnInfo(name = "username")
    var userName: String? = null,

    @ColumnInfo(name = "followers_count")
    var followerCount: Int? = null,

    @ColumnInfo(name = "following_count")
    var followingCount: Int? = null
): Parcelable
