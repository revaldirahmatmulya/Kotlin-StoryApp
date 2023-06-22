package com.revaldi.storyapp.Api

data class UserLoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult
)


