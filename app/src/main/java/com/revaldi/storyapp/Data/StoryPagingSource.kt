package com.revaldi.storyapp.Data

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.revaldi.storyapp.Api.ApiEndpoint
import com.revaldi.storyapp.Helper.PreferenceManager
import com.revaldi.storyapp.Models.StoriesData

class StoryPagingSource(private val apiService: ApiEndpoint,private val token: String) : PagingSource<Int, StoriesData>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoriesData> {
            return try {
                val position = params.key ?: INITIAL_PAGE_INDEX
                val response = apiService.getStories(token, position, params.loadSize, 0)
                val responseData = response.listStory // Menggunakan properti listStory dari respons
                LoadResult.Page(
                    data = responseData,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (responseData.isNullOrEmpty()) null else position + 1
                )
            } catch (exception: Exception) {
                LoadResult.Error(exception)
            }

    }

    override fun getRefreshKey(state: PagingState<Int, StoriesData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
