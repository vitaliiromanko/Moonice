package com.nulp.moonice.service

import com.github.javafaker.Faker
import com.nulp.moonice.R
import com.nulp.moonice.model.Book
import com.nulp.moonice.model.Genre
import java.time.LocalDateTime
import java.util.*

typealias BooksListener = (books: List<Book>) -> Unit
class BooksService {

    private var books = mutableListOf<Book>()

    private val listeners = mutableSetOf<BooksListener>()

    init {
        val faker = Faker.instance()
        books = (1..9).map {
            Book(
                id = it.toLong(),
                title = faker.book().title(),
                author = faker.book().author(),
                uploadDate = Date(1245678955 * 1000),
                description = faker.book().title(),
                genre = Genre.values()[4 % it],
                pictureLink = IMAGES[it - 1]
            ) }.toMutableList()
    }

    companion object {
        private val IMAGES = mutableListOf<Int>(
            R.drawable.book_item_10,
            R.drawable.book_item_11,
            R.drawable.book_item_12,
            R.drawable.book_item_4,
            R.drawable.book_item_5,
            R.drawable.book_item_6,
            R.drawable.book_item_7,
            R.drawable.book_item_8,
            R.drawable.book_item_9
        )
    }

    fun getBooks(): List<Book> {
        return books
    }

    fun deleteBook(book: Book) {
        val indexToDelete = books.indexOfFirst { it.id == book.id }
        if (indexToDelete != -1) {
            books.removeAt(indexToDelete)
            notifyChanges()
        }
    }

    fun addListener(listener: BooksListener) {
        listeners.add(listener)
        listener.invoke(books)
    }

    fun removeListener(listener: BooksListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        listeners.forEach{ it.invoke(books) }
    }
}