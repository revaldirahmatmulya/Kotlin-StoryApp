package com.revaldi.storyapp.Api

import com.revaldi.storyapp.Models.StoriesData

data class DetailStoriesResponse(
    val error: Boolean,
    val message: String,
    val story: StoriesData
)
