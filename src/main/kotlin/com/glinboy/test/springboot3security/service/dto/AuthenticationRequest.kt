package com.glinboy.test.springboot3security.service.dto

data class AuthenticationRequest(
    val email: String,
    val password: String
)
