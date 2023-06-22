package com.revaldi.storyapp.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.revaldi.storyapp.Data.StoryRepository
import com.revaldi.storyapp.Models.StoriesData
import com.revaldi.storyapp.di.Injection

class MainViewModel(storyRepository: StoryRepository) : ViewModel() {
    var story: LiveData<PagingData<StoriesData>> =
        storyRepository.getStoryRepo().cachedIn(viewModelScope)

}


class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}