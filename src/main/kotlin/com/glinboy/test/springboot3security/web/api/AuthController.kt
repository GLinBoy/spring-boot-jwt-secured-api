package com.glinboy.test.springboot3security.web.api

import com.glinboy.test.springboot3security.config.JwtUtils
import com.glinboy.test.springboot3security.entity.User
import com.glinboy.test.springboot3security.service.UserServiceApi
import com.glinboy.test.springboot3security.service.dto.AuthenticationRequest
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val userService: UserServiceApi,
    private val jwtUtils: JwtUtils,
    private val passwordEncoder: PasswordEncoder
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("login")
    fun authenticate(
        @RequestBody request: AuthenticationRequest,
        response: HttpServletResponse
    ): ResponseEntity<Map<String, Any>> {
        val authenticationToken: UsernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(request.email, request.password)
        val authentication: Authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)
        SecurityContextHolder.getContext().authentication = authentication
        val jwt = jwtUtils.createToken(authentication)

        val cookie = Cookie("Authorization", jwt)
        cookie.maxAge = 1000 * 86_400
//        cookie.secure = true
        cookie.isHttpOnly = true
        response.addCookie(cookie)

        val httpHeaders: HttpHeaders = HttpHeaders()
        httpHeaders.add("Authorization", "Bearer $jwt")
        return ResponseEntity(mapOf("idToken" to jwt), httpHeaders, HttpStatus.OK)
    }

    @PostMapping("register")
    fun register(@RequestBody request: AuthenticationRequest): ResponseEntity<String> {
        userService.register(
            User(
                request.email,
                passwordEncoder.encode(request.password),
                roles = "ROLE_USER"
            )
        )
        return ResponseEntity.created(URI("/auth/login")).build()
    }
}
