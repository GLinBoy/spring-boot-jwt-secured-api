package com.glinboy.test.springboot3security.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtUtils: JwtUtils
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var jwtToken: String? = resolveToken(request)
        if (StringUtils.hasText(jwtToken) && jwtUtils.validateToken(jwtToken!!)) {
            SecurityContextHolder.getContext().authentication =
                jwtUtils.getAuthentication(jwtToken)
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        var token: String? = request.cookies
            ?.singleOrNull { it.name == "Authorization" }
            ?.value
        if (!StringUtils.hasText(token)) {
            val bearerToken = request.getHeader("Authorization")
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                token = bearerToken.substring(7)
            }
        }
        return token
    }
}