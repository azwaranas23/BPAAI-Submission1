package com.dicoding.picodiploma.storyapp.preference

import android.content.Context

internal class SessionPreferences(context: Context) {
    companion object {
        private const val PREFS_NAME = "session_pref"
        private const val USER_ID = "userId"
        private const val NAME = "name"
        private const val TOKEN = "token"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setSession(value: SessionModel) {
        val editor = preferences.edit()
        editor.apply {
            putString(USER_ID, value.userId)
            putString(NAME, value.name)
            putString(TOKEN, value.token)
            apply()
        }
    }

    fun getSession(): SessionModel? {
        val model = SessionModel()
        model.apply {
            userId = preferences.getString(USER_ID, null)
            name = preferences.getString(NAME, null)
            token = preferences.getString(TOKEN, null)
        }
        return if (model.token.isNullOrEmpty()) {
            null
        } else {
            model
        }
    }

    fun getAuthToken(): String? =
        getSession()?.token?.let {
            "Bearer $it"
        }

    fun clearSession() {
        val nullSession = SessionModel()
        setSession(nullSession)
    }
}