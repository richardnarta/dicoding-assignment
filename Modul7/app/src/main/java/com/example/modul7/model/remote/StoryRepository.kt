package com.example.modul7.model.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.modul7.model.local.entity.StoryEntity
import com.example.modul7.model.local.room.StoryDatabase
import com.example.modul7.model.remote.response.DetailResponse
import com.example.modul7.model.remote.response.ListStoryItem
import com.example.modul7.model.remote.response.StoryResponse
import com.example.modul7.model.remote.response.UploadResponse
import com.example.modul7.model.remote.retrofit.ApiService
import com.example.modul7.model.remoteMediator.StoryRemoteMediator
import com.example.modul7.utils.StatusResult
import com.example.modul7.utils.StoryResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalPagingApi::class)
class StoryRepository private constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
){
    private val result = MediatorLiveData<StatusResult>()
    private val storyResult = MediatorLiveData<StoryResult<List<ListStoryItem>>>()
    private val detailStoryResult = MediatorLiveData<StoryResult<ListStoryItem>>()

    private val storyRemoteMediator = StoryRemoteMediator(storyDatabase, apiService)

    fun getStoryList (): LiveData<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = storyRemoteMediator,
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getStoryDetail (id: String): LiveData<StoryResult<ListStoryItem>> {
        detailStoryResult.value = StoryResult.Loading

        val client = apiService.getStoryDetail(id)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()

                    if (data?.error == false) {
                        val detailStoryLiveData: LiveData<ListStoryItem> = MutableLiveData(data.story)

                        detailStoryResult.addSource(detailStoryLiveData) { storyData->
                            detailStoryResult.value = StoryResult.Success(storyData)
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody!!).getString("message")
                    } catch (e: JSONException) {
                        "Invalid request"
                    }

                    detailStoryResult.value = StoryResult.Error(errorMessage)
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                detailStoryResult.value = StoryResult.Error(t.message.toString())
            }
        })

        return detailStoryResult
    }

    fun uploadUserStory (file: MultipartBody.Part,
                         description: RequestBody,
                         lat: Double?,
                         lon: Double?) : LiveData<StatusResult> {
        result.value = StatusResult.Loading

        val client = apiService.uploadStory(file, description, lat, lon)
        client.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful) {
                    val status = response.body()!!

                    if (status.error == false) {
                        result.value = StatusResult.Success("Successfully create a new account")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody!!).getString("message")
                    } catch (e: JSONException) {
                        "Invalid request"
                    }
                    result.value = StatusResult.Error(errorMessage)
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                result.value = StatusResult.Error(t.message.toString())
            }
        })

        return result
    }

    fun getStoryLocation (): LiveData<StoryResult<List<ListStoryItem>>> {
        storyResult.value = StoryResult.Loading

        val client = apiService.getStoriesLocation()
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>,
                                    response: Response<StoryResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()

                    if (data?.error == false) {
                        val storyLiveData: LiveData<List<ListStoryItem>> = MutableLiveData(data.listStory)

                        storyResult.addSource(storyLiveData) { storyData->
                            storyResult.value = StoryResult.Success(storyData)
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody!!).getString("message")
                    } catch (e: JSONException) {
                        "Invalid request"
                    }

                    storyResult.value = StoryResult.Error(errorMessage)
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                storyResult.value = StoryResult.Error(t.message.toString())
            }

        })

        return storyResult
    }

    companion object {

        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            database: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, database)
            }.also { instance = it }
    }
}