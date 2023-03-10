package com.glinboy.test.springboot3security.config

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.stream.Collectors

@Component
class JwtUtils {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val SECRET_KEY =
        "ODBkYjM0OGUyNzAwNTIwNGNmMzRjM2Y0MzNkN2FkMmMyYWVhN2IyYWFjOTgzN2VhNDMyZWQ1OTA2ZDM0NjYwMTJmNGYwOGU0ZjI4ZGMxM2M4Njc2MDhiZWEwOGQ4YjZjYmQ2YzRmYjhkZjBhNWE3MzM4NDQxZDM4OTEzMGZjMjY="
    private val jwtParser: JwtParser = Jwts.parserBuilder()
        .setSigningKey(Decoders.BASE64.decode(SECRET_KEY))
        .build()
    private val key: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY))

    fun validateToken(token: String): Boolean {
        try {
            jwtParser.parseClaimsJws(token)
            return true
        } catch (ex: ExpiredJwtException) {
            log.trace("INVALID_JWT_TOKEN", ex);
        } catch (ex: UnsupportedJwtException) {
            log.trace("INVALID_JWT_TOKEN", ex);
        } catch (ex: MalformedJwtException) {
            log.trace("INVALID_JWT_TOKEN", ex);
        } catch (ex: SecurityException) {
            log.trace("INVALID_JWT_TOKEN", ex);
        } catch (ex: IllegalArgumentException) {
            // TODO: should we let it bubble (no catch), to avoid defensive programming and follow the fail-fast principle?
            log.error("Token validation error {}", ex.message);
        }
        return false
    }

    fun getAuthentication(token: String): Authentication {
        val claims: Claims = jwtParser.parseClaimsJws(token).body
        val authorities = claims.get("auth")
            .toString()
            .split(",")
            .filter {
                it.trim().isNotEmpty()
            }
            .map {
                SimpleGrantedAuthority(it)
            }
            .toCollection(mutableListOf())
        val principal = User(claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun createToken(authentication: Authentication, rememberMe: Boolean = false): String {
        val authorities: String = authentication
            .authorities
            .stream()
            .map { it.authority }
            .collect(Collectors.joining(","))
        val now = Date().time
        var validity =
            if (rememberMe)
                Date(now + (1000 * 2_592_000))
            else
                Date(now + (1000 * 86_400))
        return Jwts
            .builder()
            .setSubject(authentication.name)
            .claim("auth", authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact()
    }
}
