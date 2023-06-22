package com.revaldi.storyapp.Utils

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.revaldi.storyapp.Models.StoriesData

class PagedTestDataSource :
    PagingSource<Int, LiveData<List<StoriesData>>>() {

    companion object {
        fun snapshot(items: List<StoriesData>): PagingData<StoriesData> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoriesData>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoriesData>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

}