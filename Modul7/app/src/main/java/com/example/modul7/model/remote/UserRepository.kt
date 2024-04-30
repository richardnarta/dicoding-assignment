package com.example.modul7.model.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.modul7.model.local.datastore.LoginSession
import com.example.modul7.model.remote.response.LoginResponse
import com.example.modul7.model.remote.response.RegisterResponse
import com.example.modul7.model.remote.retrofit.ApiService
import com.example.modul7.utils.StatusResult
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository private constructor(
    private val apiService: ApiService,
    private val loginSession: LoginSession
) {
    private val result = MediatorLiveData<StatusResult>()

    fun setLoginSession(email: String,
                     password: String): LiveData<StatusResult> {
        result.value = StatusResult.Loading

        val client = apiService.loginToAccount(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()?.loginResult
                    val status = response.body()!!

                    if (status.error == false) {
                        runBlocking {
                            if (data != null) {
                                loginSession.setLoginSession(
                                    true, data.token!!
                                )
                                result.value = StatusResult.Success(status.message!!)
                            }
                        }
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

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                result.value = StatusResult.Error(t.message.toString())
            }
        })

        return result
    }

    fun registerAccount (name: String,
                         email: String,
                         password: String): LiveData<StatusResult> {
        result.value = StatusResult.Loading

        val client = apiService.createUser(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val status = response.body()!!

                    if (status.error == false) {
                        result.value = StatusResult.Success(status.message!!)
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

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                result.value = StatusResult.Error(t.message.toString())
            }
        })

        return result
    }

    companion object {

        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            apiService: ApiService,
            loginSession: LoginSession
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, loginSession)
            }.also { instance = it }
    }
}