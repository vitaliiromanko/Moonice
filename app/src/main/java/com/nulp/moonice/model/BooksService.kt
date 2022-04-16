package com.nulp.moonice.model

import android.content.res.Resources
import com.github.javafaker.Faker
import com.nulp.moonice.R

typealias BooksListener = (books: List<Book>) -> Unit
class BooksService {

    private var books = mutableListOf<Book>()

    private val listeners = mutableSetOf<BooksListener>()

    init {
        val faker = Faker.instance()
        //IMAGES.shuffle()
        books = (1..9).map {
            Book(
                id = it.toLong(),
                title = faker.book().title(),
                author = faker.book().author(),
                uploadDate = faker.date().birthday(),
                description = faker.book().title(),
                genre = Genre.values()[4 % it],
                audioRecords = (1..10).map { recordNum ->
                    AudioRecords(
                        id = recordNum.toLong(),
                        chapterNumber = recordNum,
                        chapterTitle = "Chapter title",
                        duration = (recordNum * 100).toLong()
                    ) },
                picturePath = IMAGES[it - 1]
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