package com.glinboy.test.springboot3security.config

import com.glinboy.test.springboot3security.service.UserServiceApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
    private val userService: UserServiceApi
) {

    private val AUTH_WHITELIST = arrayOf(
        "/",
        // Public APIs
        "/api/v1/auth/**",
        // Swagger UI v3 (OpenAPI)
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        // other public endpoints of your API may be appended to this array
        "/webjars/**"
    )

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .cors().disable()
            .csrf().disable()
            .headers().frameOptions().sameOrigin()
            .and()
            .authorizeHttpRequests {
                it
                    .requestMatchers(*AUTH_WHITELIST).permitAll()
                    .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .userDetailsService(userService)
            .build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()


    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration):
            AuthenticationManager =
        authenticationConfiguration.authenticationManager
}