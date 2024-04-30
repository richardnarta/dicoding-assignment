package com.example.modul7.model.remote.response

import com.google.gson.annotations.SerializedName

data class UploadResponse (

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
