package com.revaldi.storyapp.Helper

import android.content.Context

class PreferenceManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("login_data", Context.MODE_PRIVATE)

    fun saveLoginData(userId: String,name: String, token: String) {
        sharedPreferences.edit().putString("userId", userId).apply()
        sharedPreferences.edit().putString("name", name).apply()
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("userId", null)
    }

    fun getName(): String? {
        return sharedPreferences.getString("name", null)
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", "")
    }

    fun clearLoginData() {
        sharedPreferences.edit().remove("userId").apply()
        sharedPreferences.edit().remove("name").apply()
        sharedPreferences.edit().remove("token").apply()
    }

    fun isLoggedIn(): Boolean {
        return getUserId() != null && getToken() != null && getName() != null
    }
}
