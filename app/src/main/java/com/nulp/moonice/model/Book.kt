package com.nulp.moonice.model

import java.util.*

data class Book(
    val id: Long,
    val title: String,
    val author: String,
    val uploadDate: Date,
    val description: String,
    val genre: Genre,
    val audioRecords: List<AudioRecords>
)
