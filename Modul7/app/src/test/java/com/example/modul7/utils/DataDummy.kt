package com.example.modul7.utils

import com.example.modul7.model.local.entity.StoryEntity

object DataDummy {
    fun generateStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val story = StoryEntity(
                i.toString(),
                "url + $i",
                "name $i",
                "desc $i"
            )
            items.add(story)
        }
        return items
    }
}