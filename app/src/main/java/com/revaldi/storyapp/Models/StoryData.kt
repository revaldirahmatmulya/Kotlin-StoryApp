package com.revaldi.storyapp.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoryData(
    val name: String,
    val description: String,
    val image: String,
    val lat: Float,
    val long: Float,
) : Parcelable
