package com.example.modul7.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.modul7.model.remote.StoryRepository
import com.example.modul7.utils.Event
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel (private var storyRepository: StoryRepository): ViewModel() {
    var snackBarText: MutableLiveData<Event<String>> = MutableLiveData()

    fun showStoryList() = storyRepository.getStoryList()

    fun getStoryDetail(id: String) = storyRepository.getStoryDetail(id)

    fun uploadStory(file: MultipartBody.Part, description: RequestBody) = storyRepository
                        .uploadUserStory(file, description)
}