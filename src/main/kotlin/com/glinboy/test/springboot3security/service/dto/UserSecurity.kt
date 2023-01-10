package com.glinboy.test.springboot3security.service.dto

import com.glinboy.test.springboot3security.entity.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.StringUtils

class UserSecurity(private val user: User) : UserDetails {
    override fun getAuthorities() = this.user.roles
        .split(",")
        .filter { StringUtils.hasText(it) }
        .map { SimpleGrantedAuthority(it.trim()) }

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}