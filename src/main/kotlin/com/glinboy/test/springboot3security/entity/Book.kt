package com.glinboy.test.springboot3security.entity

import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(name = "UNQ_BOOK_ISBN", columnNames = ["isbn"])
    ]
)
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var author: String,

    @Column(nullable = false)
    var isbn: String
)