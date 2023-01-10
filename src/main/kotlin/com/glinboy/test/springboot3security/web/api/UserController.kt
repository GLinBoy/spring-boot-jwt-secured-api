package com.glinboy.test.springboot3security.web.api

import com.glinboy.test.springboot3security.entity.User
import com.glinboy.test.springboot3security.service.UserServiceApi
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "Bearer Authentication")
class UserController(private val userService: UserServiceApi) {

    @GetMapping
    fun getUsers(@Parameter(hidden = true) pageable: Pageable): ResponseEntity<Page<User>> =
        ResponseEntity.ok(userService.getUsers(pageable))

    @GetMapping("{id}")
    fun getUser(@PathVariable id: String): ResponseEntity<User> =
        userService.getUser(id)
            .map { ResponseEntity.ok(it) }
            .orElseGet { ResponseEntity.notFound().build() }

    @PostMapping
    fun addUser(@RequestBody user: User): ResponseEntity<User> =
        ResponseEntity(userService.saveUser(user), HttpStatus.CREATED)

    @DeleteMapping("{id}")
    fun deleteUser(@PathVariable id: String): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping
    fun updateUser(@RequestBody user: User): ResponseEntity<User> {
        if (user.email == null) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "User should have email to update"
            )
        }
        return ResponseEntity.ok(userService.saveUser(user))
    }
}