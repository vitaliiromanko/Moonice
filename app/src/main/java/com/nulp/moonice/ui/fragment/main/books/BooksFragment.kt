package com.nulp.moonice.ui.fragment.main.books

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.nulp.moonice.adapter.BooksActionListener
import com.nulp.moonice.adapter.BooksAdapter
import com.nulp.moonice.databinding.FragmentBooksBinding
import com.nulp.moonice.model.Book
import com.nulp.moonice.ui.BookActivity
import com.nulp.moonice.utils.*


class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private lateinit var popularBooksRecyclerView: RecyclerView
    private lateinit var topComedyRecyclerView: RecyclerView
    private lateinit var topDramaRecyclerView: RecyclerView
    private lateinit var topFantasyRecyclerView: RecyclerView
    private lateinit var topRomanceRecyclerView: RecyclerView

    private lateinit var ref: DatabaseReference

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference

        popularBooksRecyclerView = binding.popularBooksRecyclerView
        popularBooksRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        initPopularBooksRecycleView(popularBooksRecyclerView)

        topComedyRecyclerView = binding.topComedyRecyclerView
        topComedyRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        initTopComedyRecycleView(topComedyRecyclerView)

        topDramaRecyclerView = binding.topDramaRecyclerView
        topDramaRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        initTopDramaRecycleView(topDramaRecyclerView)

        topFantasyRecyclerView = binding.topFantasyRecyclerView
        topFantasyRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        initTopFantasyRecycleView(topFantasyRecyclerView)

        topRomanceRecyclerView = binding.topRomanceRecyclerView
        topRomanceRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        initTopRomanceRecycleView(topRomanceRecyclerView)


        return binding.root
    }

    private fun initPopularBooksRecycleView(recyclerView: RecyclerView) {
        var bookArrayList = arrayListOf<Book>()
        ref.child(NODE_BOOKS)
            .addValueEventListener(AppValueEventListener {
                if (it.exists()) {
                    for (bookSnapshot in it.child(NODE_BOOK_DETAILS).children) {
                        if (it.child(NODE_BOOKS_TOP).child(TOP_POPULAR_BOOKS)
                                .hasChild(bookSnapshot.key!!)
                        ) {
                            val book = bookSnapshot.getValue(Book::class.java)
                            if (book != null) {
                                book.id = bookSnapshot.key?.toLong() ?: 1
                                bookArrayList.add(book)
                            }
                        }
                    }
                    bookArrayList = bookArrayList.take(10) as ArrayList<Book>
                    recyclerView.adapter = BooksAdapter((object : BooksActionListener {
                        override fun onBookClick(book: Book) {
                            val gson = Gson()
                            val intent = Intent(activity, BookActivity::class.java)
                            intent.putExtra("book", gson.toJson(book))
                            startActivity(intent)
                        }
                    }), bookArrayList)
                }

            })
    }

    private fun initTopComedyRecycleView(recyclerView: RecyclerView) {
        var bookArrayList = arrayListOf<Book>()
        ref.child(NODE_BOOKS)
            .addValueEventListener(AppValueEventListener {
                if (it.exists()) {
                    for (bookSnapshot in it.child(NODE_BOOK_DETAILS).children) {
                        if (it.child(NODE_BOOKS_TOP).child(TOP_COMEDY)
                                .hasChild(bookSnapshot.key!!)
                        ) {
                            val book = bookSnapshot.getValue(Book::class.java)
                            if (book != null) {
                                book.id = bookSnapshot.key?.toLong() ?: 1
                                bookArrayList.add(book)
                            }
                        }
                    }
                    bookArrayList = bookArrayList.take(10) as ArrayList<Book>
                    recyclerView.adapter = BooksAdapter((object : BooksActionListener {
                        override fun onBookClick(book: Book) {
                            val gson = Gson()
                            val intent = Intent(activity, BookActivity::class.java)
                            intent.putExtra("book", gson.toJson(book))
                            startActivity(intent)
                        }
                    }), bookArrayList)
                }

            })
    }

    private fun initTopDramaRecycleView(recyclerView: RecyclerView) {
        var bookArrayList = arrayListOf<Book>()
        ref.child(NODE_BOOKS)
            .addValueEventListener(AppValueEventListener {
                if (it.exists()) {
                    for (bookSnapshot in it.child(NODE_BOOK_DETAILS).children) {
                        if (it.child(NODE_BOOKS_TOP).child(TOP_DRAMA)
                                .hasChild(bookSnapshot.key!!)
                        ) {
                            val book = bookSnapshot.getValue(Book::class.java)
                            if (book != null) {
                                book.id = bookSnapshot.key?.toLong() ?: 1
                                bookArrayList.add(book)
                            }
                        }
                    }
                    bookArrayList = bookArrayList.take(10) as ArrayList<Book>
                    recyclerView.adapter = BooksAdapter((object : BooksActionListener {
                        override fun onBookClick(book: Book) {
                            val gson = Gson()
                            val intent = Intent(activity, BookActivity::class.java)
                            intent.putExtra("book", gson.toJson(book))
                            startActivity(intent)
                        }
                    }), bookArrayList)
                }

            })
    }

    private fun initTopFantasyRecycleView(recyclerView: RecyclerView) {
        var bookArrayList = arrayListOf<Book>()
        ref.child(NODE_BOOKS)
            .addValueEventListener(AppValueEventListener {
                if (it.exists()) {
                    for (bookSnapshot in it.child(NODE_BOOK_DETAILS).children) {
                        if (it.child(NODE_BOOKS_TOP).child(TOP_FANTASY)
                                .hasChild(bookSnapshot.key!!)
                        ) {
                            val book = bookSnapshot.getValue(Book::class.java)
                            if (book != null) {
                                book.id = bookSnapshot.key?.toLong() ?: 1
                                bookArrayList.add(book)
                            }
                        }
                    }
                    bookArrayList = bookArrayList.take(10) as ArrayList<Book>
                    recyclerView.adapter = BooksAdapter((object : BooksActionListener {
                        override fun onBookClick(book: Book) {
                            val gson = Gson()
                            val intent = Intent(activity, BookActivity::class.java)
                            intent.putExtra("book", gson.toJson(book))
                            startActivity(intent)
                        }
                    }), bookArrayList)
                }

            })
    }

    private fun initTopRomanceRecycleView(recyclerView: RecyclerView) {
        var bookArrayList = arrayListOf<Book>()
        ref.child(NODE_BOOKS)
            .addValueEventListener(AppValueEventListener {
                if (it.exists()) {
                    for (bookSnapshot in it.child(NODE_BOOK_DETAILS).children) {
                        if (it.child(NODE_BOOKS_TOP).child(TOP_ROMANCE)
                                .hasChild(bookSnapshot.key!!)
                        ) {
                            val book = bookSnapshot.getValue(Book::class.java)
                            if (book != null) {
                                book.id = bookSnapshot.key?.toLong() ?: 1
                                bookArrayList.add(book)
                            }
                        }
                    }
                    bookArrayList = bookArrayList.take(10) as ArrayList<Book>
                    recyclerView.adapter = BooksAdapter((object : BooksActionListener {
                        override fun onBookClick(book: Book) {
                            val gson = Gson()
                            val intent = Intent(activity, BookActivity::class.java)
                            intent.putExtra("book", gson.toJson(book))
                            startActivity(intent)
                        }
                    }), bookArrayList)
                }

            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}