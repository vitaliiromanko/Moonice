package com.nulp.moonice.service

import android.util.Log
import com.github.javafaker.Faker
import com.nulp.moonice.model.AudioRecord
import com.nulp.moonice.model.Book
import com.nulp.moonice.model.Genre

typealias AudioRecordsListener = (records: List<AudioRecord>) -> Unit

class AudioRecordsService {

    private var records = mutableListOf<AudioRecord>()

    private val listeners = mutableSetOf<AudioRecordsListener>()

    fun getRecords(): List<AudioRecord> {
        return this.records
    }

    fun setRecords(book: Book) {
        val faker = Faker.instance()
        this.records = (1..9).map {
            AudioRecord(
                id = it.toLong(),
                chapterNumber = it,
                chapterTitle = faker.book().title(),
                duration = it.toLong(),
                like = it + 10,
                book = book.id,
                recordLink = ""
            )
        }.toMutableList()
    }

    fun cleanRecords() {
        this.records = emptyList<AudioRecord>().toMutableList()
    }

    fun addListener(listener: AudioRecordsListener) {
        listeners.add(listener)
        listener.invoke(records)
    }

    fun removeListener(listener: AudioRecordsListener) {
        listeners.remove(listener)
    }
}