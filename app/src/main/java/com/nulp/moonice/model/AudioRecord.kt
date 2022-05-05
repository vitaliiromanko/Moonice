package com.nulp.moonice.model

import java.net.URL

data class AudioRecord(
    val id: Long,
    val chapterNumber: Int,
    val chapterTitle: String?,
    val duration: Long,
    val like: Int,
    val book: Long,
    val recordLink: String
)
