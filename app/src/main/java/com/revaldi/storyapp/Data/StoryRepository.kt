package com.revaldi.storyapp.Data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.revaldi.storyapp.Api.ApiEndpoint
import com.revaldi.storyapp.Api.StoriesResponse
import com.revaldi.storyapp.Database.StoryDatabase
import com.revaldi.storyapp.Helper.PreferenceManager
import com.revaldi.storyapp.Models.StoriesData
import com.revaldi.storyapp.Models.StoryData

open class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiEndpoint, private val token:String) {
    open fun getStoryRepo(): LiveData<PagingData<StoriesData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService,token)
            }
        ).liveData
    }
}