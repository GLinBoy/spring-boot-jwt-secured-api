package com.glinboy.test.springboot3security.service.impl

import com.glinboy.test.springboot3security.entity.Book
import com.glinboy.test.springboot3security.repository.BookRepository
import com.glinboy.test.springboot3security.service.BookServiceApi
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class BookServiceImpl(val bookRepository: BookRepository): BookServiceApi {
    override fun getBooks(pageable: Pageable): Page<Book> = bookRepository.findAll(pageable)

    override fun getBook(id: Long): Optional<Book> = bookRepository.findById(id)
    override fun saveBook(book: Book): Book = bookRepository.save(book)
    override fun deleteBook(id: Long) = bookRepository.deleteById(id)
}