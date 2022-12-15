package com.glinboy.test.springboot3security.repository

import com.glinboy.test.springboot3security.entity.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository: JpaRepository<Book, Long> {
}