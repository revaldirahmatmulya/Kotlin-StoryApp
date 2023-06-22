package com.revaldi.storyapp.Api
import com.revaldi.storyapp.Models.StoriesData

data class StoriesResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<StoriesData>
    )
