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
import com.nulp.moonice.utils.AppValueEventListener
import com.nulp.moonice.utils.FIREBASE_URL
import com.nulp.moonice.utils.NODE_BOOKS
import com.nulp.moonice.utils.NODE_BOOK_DETAILS


class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private lateinit var bookArrayList: ArrayList<Book>
    private lateinit var bookRecyclerView: RecyclerView
    private lateinit var ref: DatabaseReference

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)

        bookRecyclerView = binding.recyclerView1
        bookRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        bookArrayList = arrayListOf<Book>()

        initRecycleView(bookRecyclerView)
        return binding.root
    }

    private fun initRecycleView(recyclerView: RecyclerView) {
        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
        ref.child(NODE_BOOKS).child(NODE_BOOK_DETAILS)
            .addValueEventListener(AppValueEventListener {
                if (it.exists()) {
                    for (bookSnapshot in it.children) {
                        val book = bookSnapshot.getValue(Book::class.java)
                        if (book != null) {
                            book.id = bookSnapshot.key?.toLong() ?: 1
                            bookArrayList.add(book)
                        }
                    }
//                        bookArrayList = bookArrayList.filter { it.genre == 1 } as ArrayList<Book>
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