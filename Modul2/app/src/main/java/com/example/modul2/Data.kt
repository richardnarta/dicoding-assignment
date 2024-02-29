package com.example.modul2

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray

object Data {
    fun getData(context: Context): ArrayList<Manga>{
        val mangaList = arrayListOf<Manga>()

        val image:TypedArray = context.resources.obtainTypedArray(R.array.mangaCover)
        val title:Array<String> = context.resources.getStringArray(R.array.mangaTitle)
        val rating:Array<String> = context.resources.getStringArray(R.array.mangaRating)
        val chapter:Array<String> = context.resources.getStringArray(R.array.mangaChapter)
        val synopsis:Array<String> = context.resources.getStringArray(R.array.mangaSynopsis)

        for (i in 0..9){
            mangaList.add(Manga(image.getResourceId(i, -1), title[i], chapter[i], rating[i], synopsis[i]))
        }
        image.recycle()

        return mangaList
    }
}