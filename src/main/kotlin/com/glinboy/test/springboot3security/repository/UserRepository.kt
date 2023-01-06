package com.glinboy.test.springboot3security.repository

import com.glinboy.test.springboot3security.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, String> {
}