package com.nulp.moonice.ui.fragment.main.bookmarks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.nulp.moonice.adapter.BookmarkActionListener
import com.nulp.moonice.adapter.BookmarkAdapter
import com.nulp.moonice.databinding.FragmentBookmarksBinding
import com.nulp.moonice.model.AudioRecord
import com.nulp.moonice.model.Book
import com.nulp.moonice.model.Bookmark
import com.nulp.moonice.ui.PlayerActivity
import com.nulp.moonice.utils.*

class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null

    private lateinit var bookmarksRecyclerView: RecyclerView
    private lateinit var bookmarksArrayList: ArrayList<Bookmark>

    private lateinit var ref: DatabaseReference
    private lateinit var user: FirebaseUser
    private lateinit var bookmarkRef: DatabaseReference
    private lateinit var recordRef: DatabaseReference
    private lateinit var bookRef: DatabaseReference
    private lateinit var records: DataSnapshot
    private lateinit var books: DataSnapshot

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)

        user = FirebaseAuth.getInstance().currentUser!!
        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
        bookmarkRef = ref.child(NODE_USERS).child(NODE_BOOKMARKS)
        recordRef = ref.child(NODE_BOOKS).child(NODE_RECORDS)
        bookRef = ref.child(NODE_BOOKS).child(NODE_BOOK_DETAILS)

        bookmarksArrayList = arrayListOf()

        recordRef.addValueEventListener(AppValueEventListener {
            records = it
        })

        bookRef.addValueEventListener(AppValueEventListener {
            books = it
        })

        bookmarksRecyclerView = binding.bookmarksRecyclerView
        val bookmarksLayoutManager = object : LinearLayoutManager(activity, VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        bookmarksRecyclerView.layoutManager = bookmarksLayoutManager

        initBookmarksRecyclerView(bookmarksRecyclerView)

        return binding.root
    }

    private fun initBookmarksRecyclerView(recyclerView: RecyclerView) {
        bookmarkRef.child(user.uid)
            .addValueEventListener(AppValueEventListener {
                bookmarksArrayList.clear()
                if (it.exists()) {
                    for (bookmarkSnapshot in it.children) {
                        val mark = bookmarkSnapshot.getValue(Long::class.java)
                        val recordSnapshot = records.child(mark!!.toString())
                        val record = recordSnapshot.getValue(AudioRecord::class.java)
                        record!!.id = recordSnapshot.key?.toLong()

                        val bookSnapshot = books.child(record.book.toString())
                        val book = bookSnapshot.getValue(Book::class.java)
                        book!!.id = bookSnapshot.key?.toLong()

                        val cBookmark = Bookmark()
                        cBookmark.record = record
                        cBookmark.book = book

                        bookmarksArrayList.add(cBookmark)
                    }
                }
                recyclerView.adapter = BookmarkAdapter((object : BookmarkActionListener {
                    override fun onBookmarkClick(bookmark: Bookmark) {
                        val gson = Gson()
                        val intent = Intent(activity, PlayerActivity::class.java)
                        intent.putExtra("record", gson.toJson(bookmark.record))
                        startActivity(intent)
                    }
                }), bookmarksArrayList.reversed())
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}