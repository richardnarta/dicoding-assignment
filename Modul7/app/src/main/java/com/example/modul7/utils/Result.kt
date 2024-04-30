package com.example.modul7.utils

sealed class StatusResult {
    data class Success(val success: String) : StatusResult()
    data class Error(val error: String) : StatusResult()
    data object Loading : StatusResult()
}

sealed class StoryResult<out R> {
    data class Success<out T>(val data: T) : StoryResult<T>()
    data class Error(val error: String) : StoryResult<Nothing>()
    data object Loading : StoryResult<Nothing>()
}