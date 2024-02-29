package com.example.modul2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Manga(
    val image:Int,
    val title:String,
    val chapter:String,
    val rating:String,
    val synopsis:String
):Parcelable
