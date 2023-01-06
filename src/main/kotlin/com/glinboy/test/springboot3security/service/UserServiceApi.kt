package com.glinboy.test.springboot3security.service

import com.glinboy.test.springboot3security.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetailsService
import java.util.*

interface UserServiceApi: UserDetailsService {
    fun getUsers(pageable: Pageable): Page<User>
    fun getUser(id: String): Optional<User>
    fun saveUser(book: User): User
    fun deleteUser(id: String)
    fun register(user: User)
}