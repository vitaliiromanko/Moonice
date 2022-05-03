package com.nulp.moonice.model

data class AudioRecord(
    val id: Long,
    val chapterNumber: Int,
    val chapterTitle: String?,
    val duration: Long,
    val like: Int,
    val book: Book,
    val recordLink: String
)
