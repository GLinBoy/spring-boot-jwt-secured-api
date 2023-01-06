package com.glinboy.test.springboot3security.service

import com.glinboy.test.springboot3security.entity.User
import org.springframework.security.core.userdetails.UserDetailsService

interface UserServiceApi: UserDetailsService {
    fun register(user: User)
}