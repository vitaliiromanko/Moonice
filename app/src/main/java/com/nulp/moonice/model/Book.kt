package com.nulp.moonice.model

data class Book(
    var id: Long? = null,
    var title: String? = null,
    var author: String? = null,
    var publishDate: Int? = null,
    var description: String? = null,
    var genre: Int? = null,
    var pictureLink: String? = null
)
