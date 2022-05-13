package com.nulp.moonice.ui.fragment.main.search

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.nulp.moonice.R
import com.nulp.moonice.adapter.BooksActionListener
import com.nulp.moonice.adapter.BooksAdapter
import com.nulp.moonice.databinding.FragmentSearchBinding
import com.nulp.moonice.model.Book
import com.nulp.moonice.ui.BookActivity
import com.nulp.moonice.utils.AppValueEventListener
import com.nulp.moonice.utils.FIREBASE_URL
import com.nulp.moonice.utils.NODE_BOOKS
import com.nulp.moonice.utils.NODE_BOOK_DETAILS


// performs search operation on all book items from firebase, uses Book and BooksAdapter
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null

    private lateinit var searchView: SearchView
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var bookArrayList: ArrayList<Book>
    private lateinit var gridLayoutManager: GridLayoutManager
    private val columnWidthDp = 150

    private lateinit var ref: DatabaseReference

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference

        searchView = requireActivity().findViewById(R.id.search_view)
        bookArrayList = arrayListOf()

        searchRecyclerView = binding.booksRecyclerView
        gridLayoutManager = GridLayoutManager(activity, calculateNoOfColumns(requireActivity()))
        searchRecyclerView.layoutManager = gridLayoutManager
        initSearchRecycleView(searchRecyclerView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                searchView.clearFocus()
                if (query != null) {
                    search(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    search(newText)
                }
                return false
            }

        })

        return binding.root
    }

    private fun initSearchRecycleView(recyclerView: RecyclerView) {
        ref.child(NODE_BOOKS)
            .addValueEventListener(AppValueEventListener {
                if (it.exists()) {
                    bookArrayList.clear()
                    for (bookSnapshot in it.child(NODE_BOOK_DETAILS).children) {
                        val book = bookSnapshot.getValue(Book::class.java)
                        if (book != null) {
                            book.id = bookSnapshot.key?.toLong() ?: 1
                            bookArrayList.add(book)
                        }
                    }
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

    private fun search(str: String) {
        val foundList = arrayListOf<Book>()
        for (book in bookArrayList) {
            if (book.title!!.lowercase().contains(str.lowercase())) {
                foundList.add(book)
            }
        }
        searchRecyclerView.adapter = BooksAdapter((object : BooksActionListener {
            override fun onBookClick(book: Book) {
                val gson = Gson()
                val intent = Intent(activity, BookActivity::class.java)
                intent.putExtra("book", gson.toJson(book))
                startActivity(intent)
            }
        }), foundList)
    }

    private fun calculateNoOfColumns(
        context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels.toFloat() / displayMetrics.density - 20
        return (screenWidthDp / columnWidthDp.toFloat() + 0.5).toInt()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        searchRecyclerView = binding.booksRecyclerView
        gridLayoutManager = GridLayoutManager(activity, calculateNoOfColumns(requireActivity()))
        searchRecyclerView.layoutManager = gridLayoutManager
        initSearchRecycleView(searchRecyclerView)
    }
}