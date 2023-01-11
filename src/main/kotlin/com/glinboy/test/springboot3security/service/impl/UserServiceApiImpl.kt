package com.glinboy.test.springboot3security.service.impl

import com.glinboy.test.springboot3security.entity.User
import com.glinboy.test.springboot3security.repository.UserRepository
import com.glinboy.test.springboot3security.service.UserServiceApi
import com.glinboy.test.springboot3security.service.dto.UserSecurity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserServiceImpl(private val repository: UserRepository): UserServiceApi {
    override fun register(user: User) {
        repository.findById(user.email).ifPresent {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Email exist: %s".format(user.email)
            )
        }
        repository.save(user)
    }

    override fun loadUserByUsername(username: String): UserDetails =
        repository.findById(username)
            .map { UserSecurity(it) }
            .orElseThrow { UsernameNotFoundException("User not found") }

    override fun getUsers(pageable: Pageable): Page<User> {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        if (auth.authorities.stream().anyMatch { it.authority == "ROLE_ADMIN" }) {
            return repository.findAll(pageable)
        } else {
            throw ResponseStatusException(HttpStatus.FORBIDDEN,
                "You don't have permit to get information of this user")
        }
    }

    override fun getUser(id: String): Optional<User> = repository.findById(id)
    override fun saveUser(user: User): User = repository.save(user)
    override fun deleteUser(id: String) = repository.deleteById(id)
}