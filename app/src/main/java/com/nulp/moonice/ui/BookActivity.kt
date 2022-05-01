package com.nulp.moonice.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.nulp.moonice.App
import com.nulp.moonice.adapter.AudioRecordsActionListener
import com.nulp.moonice.adapter.AudioRecordsAdapter
import com.nulp.moonice.databinding.ActivityBookBinding
import com.nulp.moonice.model.AudioRecord
import com.nulp.moonice.model.Book
import com.nulp.moonice.service.AudioRecordsListener
import com.nulp.moonice.service.AudioRecordsService
import java.text.DateFormat
import java.text.SimpleDateFormat

class BookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookBinding
    private lateinit var adapter: AudioRecordsAdapter
    private val recordsService: AudioRecordsService
        get() = (applicationContext as App).audioRecordsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)
        val gson = Gson()
        val book = gson.fromJson(intent.getStringExtra("book"), Book::class.java)

        initPage(binding, book)

        initRecycleView(binding, book)
    }

    private fun initPage(binding: ActivityBookBinding, book: Book) {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy")
        val strDate = dateFormat.format(book.uploadDate)

        binding.bookTitleMain.text = book.title
        binding.navHeaderBook.bookTitleHeaderBar.text = book.title
        binding.navHeaderBook.bookAuthorHeaderBar.text = "${book.author} $strDate"
        binding.navHeaderBook.bookDescriptionHeaderBar.text = book.description
        binding.navHeaderBook.bookGenreHeaderBar.text = book.genre.genreName
        binding.navHeaderBook.navHeaderBookBookImage.setImageResource(book.picturePath)

        binding.activityBookBackButton.setOnClickListener {
            finish()
        }
    }

    private fun initRecycleView(binding: ActivityBookBinding, book: Book) {
        recordsService.setRecords(book)

        adapter = AudioRecordsAdapter(object : AudioRecordsActionListener {
            override fun onRecordClick(record: AudioRecord) {
                val gson = Gson()
                val intent = Intent(this@BookActivity, PlayerActivity::class.java)
                intent.putExtra("record", gson.toJson(record))
                startActivity(intent)
            }
        })
        binding.chapterList.listOfRecords.layoutManager = LinearLayoutManager(this)
        binding.chapterList.listOfRecords.adapter = adapter

        recordsService.addListener(recordsListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        recordsService.removeListener(recordsListener)
    }

    private val recordsListener: AudioRecordsListener = {
        adapter.records = it
    }
}