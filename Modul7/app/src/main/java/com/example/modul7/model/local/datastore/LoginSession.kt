package com.example.modul7.model.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class LoginSession private constructor (private val dataStore: DataStore<Preferences>) {

    companion object {
        @Volatile
        private var INSTANCE: LoginSession? = null

        fun getInstance(dataStore: DataStore<Preferences>): LoginSession {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginSession(dataStore)
                INSTANCE = instance
                instance
            }
        }

        val SESSION_TOKEN = stringPreferencesKey("session_token")
        val LOGIN_SESSION = booleanPreferencesKey("login_session")
    }

    suspend fun setLoginSession (isLogin: Boolean, token: String) {
        dataStore.edit { preferences->
            preferences[LOGIN_SESSION] = isLogin
            preferences[SESSION_TOKEN] = token
        }
    }

    fun getLoginInfo(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_SESSION] ?: false
        }
    }

    fun getSessionToken(): Flow<String> {
        return dataStore.data.map {preferences->
            preferences[SESSION_TOKEN] ?: "null"
        }
    }
}