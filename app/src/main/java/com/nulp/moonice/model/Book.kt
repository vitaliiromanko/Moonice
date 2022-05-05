package com.nulp.moonice.model

import java.net.URL
import java.util.*

data class Book(
    val id: Long,
    val title: String,
    val author: String,
    val publishDate: Int,
    val description: String,
    val genre: Long,
    val pictureLink: String
)
