package com.glinboy.test.springboot3security.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity(name = "Users")
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(name = "email_unique", columnNames = ["email"])
    ]
)
data class User(
    @Id
    val email: String,
    val password: String,
    val roles: String
)
