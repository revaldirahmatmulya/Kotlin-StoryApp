package com.revaldi.storyapp.di

import android.content.Context
import com.revaldi.storyapp.Api.ApiConfig
import com.revaldi.storyapp.Data.StoryRepository
import com.revaldi.storyapp.Database.StoryDatabase
import com.revaldi.storyapp.Helper.PreferenceManager

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.instanceRetrofit
        val token = "Bearer ${PreferenceManager(context).getToken()}"
        return StoryRepository(database, apiService, token)
    }
}