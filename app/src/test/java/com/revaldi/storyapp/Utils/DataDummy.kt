package com.revaldi.storyapp.Utils

import com.revaldi.storyapp.Models.StoriesData


object DataDummy {

    fun generateDummyListStory(): List<StoriesData> {
        val stories: MutableList<StoriesData> = arrayListOf()
        for (i in 0..100) {
            val story = StoriesData(
                i.toString(),
                "name + $i",
                "2021-10-30T07:00:00.000Z",
                "description + $i",
                "https://picsum.photos/200/300",
                1.0,
                1.0
            )
            stories.add(story)
        }
        return stories
    }



}