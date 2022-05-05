package com.nulp.moonice.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.gson.Gson
import com.nulp.moonice.App
import com.nulp.moonice.adapter.AudioRecordsActionListener
import com.nulp.moonice.adapter.AudioRecordsAdapter
import com.nulp.moonice.databinding.ActivityBookBinding
import com.nulp.moonice.model.AudioRecord
import com.nulp.moonice.model.Book
import com.nulp.moonice.service.AudioRecordsListener
import com.nulp.moonice.service.AudioRecordsService
import com.nulp.moonice.utils.FIREBASE_URL
import com.nulp.moonice.utils.NODE_BOOKS
import com.nulp.moonice.utils.NODE_GENRE
import java.text.DateFormat
import java.text.SimpleDateFormat

class BookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookBinding
    private lateinit var adapter: AudioRecordsAdapter
    private val recordsService: AudioRecordsService
        get() = (applicationContext as App).audioRecordsService
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookBinding.inflate(layoutInflater)
        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)
        val gson = Gson()
        val book = gson.fromJson(intent.getStringExtra("book"), Book::class.java)


        initPage(binding, book)

        initRecycleView(binding, book)
    }

    private fun initPage(binding: ActivityBookBinding, book: Book) {
        val genreKey = book.genre
        binding.bookTitleMain.text = book.title
        binding.navHeaderBook.bookTitleHeaderBar.text = book.title
        binding.navHeaderBook.bookAuthorHeaderBar.text = "${book.author} ${book.publishDate}"
        binding.navHeaderBook.bookDescriptionHeaderBar.text = book.description
        ref.child(NODE_BOOKS).child(NODE_GENRE).child(genreKey.toString()).addValueEventListener( object  :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.navHeaderBook.bookGenreHeaderBar.text = snapshot.value as String
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("errorBookGenre", "couldn't retrieve genre from DB")
            }

        })
//        binding.navHeaderBook.bookGenreHeaderBar.text = genreValue.toString()
        binding.navHeaderBook.navHeaderBookBookImage.setImageURI(Uri.parse(book.pictureLink))

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