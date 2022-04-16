package com.nulp.moonice

import android.app.Application
import com.nulp.moonice.model.BooksService

class App : Application() {
    val booksService = BooksService()
}