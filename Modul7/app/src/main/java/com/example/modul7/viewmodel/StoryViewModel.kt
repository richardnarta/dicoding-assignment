package com.example.modul7.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.modul7.model.local.entity.StoryEntity
import com.example.modul7.model.remote.StoryRepository
import com.example.modul7.utils.Event
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel (private var storyRepository: StoryRepository): ViewModel() {
    var snackBarText: MutableLiveData<Event<String>> = MutableLiveData()
    private val _refreshTrigger = MutableLiveData<Unit>()

    init {
        refresh()
    }

    val storyList: LiveData<PagingData<StoryEntity>> =_refreshTrigger.switchMap {
        storyRepository.getStoryList().cachedIn(viewModelScope)
    }

    fun refresh() {
        _refreshTrigger.value = Unit
    }

    fun getStoryDetail(id: String) = storyRepository.getStoryDetail(id)

    fun uploadStory(file: MultipartBody.Part, description: RequestBody, lat: Double?, lon: Double?) = storyRepository
                        .uploadUserStory(file, description, lat, lon)

    fun getStoryLocation() = storyRepository.getStoryLocation()
}