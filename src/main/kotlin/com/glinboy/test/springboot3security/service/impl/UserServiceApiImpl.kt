package com.glinboy.test.springboot3security.service.impl

import com.glinboy.test.springboot3security.entity.User
import com.glinboy.test.springboot3security.repository.UserRepository
import com.glinboy.test.springboot3security.service.UserServiceApi
import com.glinboy.test.springboot3security.service.dto.UserSecurity
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserServiceImpl(private val repository: UserRepository): UserServiceApi {
    override fun register(user: User) {
        repository.findById(user.email).ifPresent {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Email exist: %s".format(user.email))
        }
            repository.save(user)
    }

    override fun loadUserByUsername(username: String): UserDetails =
        repository.findById(username)
            .map { UserSecurity(it) }
            .orElseThrow { UsernameNotFoundException("User not found") }
}