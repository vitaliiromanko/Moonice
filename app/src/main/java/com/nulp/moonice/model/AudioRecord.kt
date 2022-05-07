package com.nulp.moonice.model

data class AudioRecord(
    var id: Long ?= null,
    var chapterNumber: Int ?= null,
    var chapterTitle: String ?= null,
    var duration: Long ?= null,
    var like: Int ?= null,
    var book: Long ?= null,
    var recordLink: String ?= null
)
