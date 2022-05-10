package com.nulp.moonice.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.nulp.moonice.R
import com.nulp.moonice.adapter.AudioRecordsActionListener
import com.nulp.moonice.adapter.AudioRecordsAdapter
import com.nulp.moonice.databinding.ActivityBookBinding
import com.nulp.moonice.model.AudioRecord
import com.nulp.moonice.model.Book
import com.nulp.moonice.utils.*
import com.squareup.picasso.Picasso
import java.util.*

class BookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookBinding
    private lateinit var ref: DatabaseReference
    private lateinit var bookmarkRef: DatabaseReference
    private lateinit var audioRecordArrayList: ArrayList<AudioRecord>
    private lateinit var audioRecordRecyclerView: RecyclerView

    private lateinit var user: FirebaseUser

    private lateinit var book: Book

    private var hasBookmark = false
    private lateinit var userBookmarks: DataSnapshot
    private lateinit var records: DataSnapshot
    private var minChapterNumber = Int.MAX_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)

        user = FirebaseAuth.getInstance().currentUser!!
        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
        bookmarkRef = ref.child(NODE_USERS).child(NODE_BOOKMARKS)


        val gson = Gson()
        book = gson.fromJson(intent.getStringExtra("book"), Book::class.java)
        audioRecordRecyclerView = binding.chapterList.listOfRecords
        audioRecordRecyclerView.layoutManager = LinearLayoutManager(this)
        audioRecordArrayList = arrayListOf()

        binding.navHeaderBook.bookDescriptionHeaderBar.movementMethod = ScrollingMovementMethod()

        ref.child(NODE_BOOKS).child(NODE_RECORDS)
            .addValueEventListener(AppValueEventListener {
                records = it
                if (it.exists()) {
                    for (recordSnapshot in it.children) {
                        val record =
                            recordSnapshot.getValue(AudioRecord::class.java)
                        if (record!!.book == book.id && minChapterNumber > record.chapterNumber!!) {
                            minChapterNumber = record.chapterNumber!!
                        }
                    }
                }
            })

        initPage(binding)

        initRecycleView(audioRecordRecyclerView)

        binding.bookmark.setOnClickListener {
            bookmark(binding.bookmark)
        }
    }

    private fun bookmark(bookmark: ImageButton) {
        Log.d("Test", "Space")
        Log.d("Test", "First Has bookmark: $hasBookmark")


        if (hasBookmark) {
            for (markSnapshot in userBookmarks.children) {
                val mark = markSnapshot.getValue(Long::class.java)
                val record = records.child(mark!!.toString())
                    .getValue(AudioRecord::class.java)
                if (record!!.book == book.id && record.chapterNumber == minChapterNumber) {
                    bookmarkRef.child(user.uid)
                        .child(markSnapshot.key.toString()).removeValue()
                    bookmark.setImageResource(R.drawable.ic_bookmark_border)
                    hasBookmark = false
                    Log.d("Test", "Second Has bookmark: $hasBookmark")
                    break
                }
            }
        } else {
            if (records.exists()) {
                for (recordSnapshot in records.children) {
                    val record =
                        recordSnapshot.getValue(AudioRecord::class.java)
                    record!!.id = recordSnapshot.key?.toLong()
                    if (record.book == book.id && record.chapterNumber == minChapterNumber) {

                        Log.d("Test", record.toString())

                        bookmarkRef.child(user.uid)
                            .child(Date().time.toString())
                            .setValue(record.id)
                        Log.d("Test", "Third Has bookmark: $hasBookmark")
                        break
                    }
                }
            }
        }
    }

    private fun initPage(binding: ActivityBookBinding) {
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

        bookmarkRef.child(user.uid)
            .addValueEventListener(AppValueEventListener {
                userBookmarks = it
                var setIcon = false
                if (it.exists()) {
                    for (markSnapshot in it.children) {
                        val mark = markSnapshot.getValue(Long::class.java)
                        val record = records.child(mark!!.toString())
                            .getValue(AudioRecord::class.java)
                        if (record!!.book == book.id && record.chapterNumber == minChapterNumber) {
                            binding.bookmark.setImageResource(R.drawable.ic_bookmark)
                            hasBookmark = true
                            setIcon = true
                            break
                        }
                    }
                }
                if (!setIcon) {
                    binding.bookmark.setImageResource(R.drawable.ic_bookmark_border)
                    hasBookmark = false
                }
            })

        binding.activityBookBackButton.setOnClickListener {
            finish()
        }
    }


    private fun initRecycleView(audioRecordRecyclerView: RecyclerView) {
        ref.child(NODE_BOOKS).child(NODE_RECORDS)
            .addValueEventListener(AppValueEventListener { it ->
                if (it.exists()) {
                    audioRecordArrayList.clear()
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
}