package com.glinboy.test.springboot3security.config

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Collectors
import javax.crypto.SecretKey

@Component
class JwtUtils {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${application.secret}")
    lateinit var SECRET_KEY: String

    fun validateToken(token: String): Boolean {
        val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY))
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
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
        val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY))
        val claims: Claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token).payload
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
        val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY))
        val authorities: String = authentication
            .authorities
            .stream()
            .map { it.authority }
            .collect(Collectors.joining(","))
        val now = Date().time
        val validity =
            if (rememberMe)
                Date(now + (2_592_000_000))
            else
                Date(now + (86_400_000))
        return Jwts
            .builder()
            .subject(authentication.name)
            .claim("auth", authorities)
            .expiration(validity)
            .signWith(key, Jwts.SIG.HS512)
            .compact()
    }
}
