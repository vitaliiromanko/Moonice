package com.nulp.moonice.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.nulp.moonice.adapter.AudioRecordsActionListener
import com.nulp.moonice.adapter.AudioRecordsAdapter
import com.nulp.moonice.databinding.ActivityBookBinding
import com.nulp.moonice.model.AudioRecord
import com.nulp.moonice.model.Book
import com.nulp.moonice.utils.*
import com.squareup.picasso.Picasso

class BookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookBinding
    private lateinit var adapter: AudioRecordsAdapter

    //    private val recordsService: AudioRecordsService
//        get() = (applicationContext as App).audioRecordsService
    private lateinit var ref: DatabaseReference
    private lateinit var audioRecordArrayList: ArrayList<AudioRecord>
    private lateinit var audioRecordRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookBinding.inflate(layoutInflater)
        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)
        val gson = Gson()
        val book = gson.fromJson(intent.getStringExtra("book"), Book::class.java)
        audioRecordRecyclerView = binding.chapterList.listOfRecords
        audioRecordRecyclerView.layoutManager = LinearLayoutManager(this)
        audioRecordArrayList = arrayListOf()

        initPage(binding, book)

        initRecycleView(book, audioRecordRecyclerView)

//        initRecycleView(binding, book)
    }

    private fun initPage(binding: ActivityBookBinding, book: Book) {
        val genreKey = book.genre
        binding.bookTitleMain.text = book.title
        binding.navHeaderBook.bookTitleHeaderBar.text = book.title
        binding.navHeaderBook.bookAuthorHeaderBar.text = "${book.author} ${book.publishDate}"
        binding.navHeaderBook.bookDescriptionHeaderBar.text = book.description
        ref.child(NODE_BOOKS).child(NODE_GENRE).child(genreKey.toString())
            .addValueEventListener(AppValueEventListener {
                binding.navHeaderBook.bookGenreHeaderBar.text = it.value as String
            })
//        binding.navHeaderBook.bookGenreHeaderBar.text = genreValue.toString()
        Picasso.get().load(book.pictureLink).into(binding.navHeaderBook.navHeaderBookBookImage)

        binding.activityBookBackButton.setOnClickListener {
            finish()
        }
    }


    private fun initRecycleView(book: Book, audioRecordRecyclerView: RecyclerView) {
        ref.child(NODE_BOOKS).child(NODE_RECORDS)
            .addValueEventListener(AppValueEventListener { it ->
                if (it.exists()) {
                    for (audioRecordSnapshot in it.children) {
                        val audioRecord = audioRecordSnapshot.getValue(AudioRecord::class.java)
                        if (audioRecord != null) {
                            audioRecord.id = audioRecordSnapshot.key?.toLong() ?: 1
                            audioRecordArrayList.add(audioRecord)
                        }
                    }
                    audioRecordArrayList =
                        audioRecordArrayList.filter { it.book == book.id } as ArrayList<AudioRecord>
                    audioRecordRecyclerView.adapter =
                        AudioRecordsAdapter((object : AudioRecordsActionListener {
                            override fun onRecordClick(record: AudioRecord) {
                                val gson = Gson()
                                val intent =
                                    Intent(this@BookActivity, PlayerActivity::class.java)
                                intent.putExtra("record", gson.toJson(record))
                                startActivity(intent)
                            }

                        }), audioRecordArrayList)
                }

            })
    }


//    private fun initRecycleView(binding: ActivityBookBinding, book: Book) {
////        recordsService.setRecords(book)
//
//        adapter = AudioRecordsAdapter(object : AudioRecordsActionListener {
//            override fun onRecordClick(record: AudioRecord) {
//                val gson = Gson()
//                val intent = Intent(this@BookActivity, PlayerActivity::class.java)
//                intent.putExtra("record", gson.toJson(record))
//                startActivity(intent)
//            }
//        })
//        binding.chapterList.listOfRecords.layoutManager = LinearLayoutManager(this)
//        binding.chapterList.listOfRecords.adapter = adapter
//
////        recordsService.addListener(recordsListener)
//    }

    override fun onDestroy() {
        super.onDestroy()
//        recordsService.removeListener(recordsListener)
    }

//    private val recordsListener: AudioRecordsListener = {
//        adapter.records = it
//    }
}