package com.revaldi.storyapp.Api

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)
