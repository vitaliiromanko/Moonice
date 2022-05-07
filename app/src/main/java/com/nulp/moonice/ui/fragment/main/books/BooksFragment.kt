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
    private lateinit var adapter: BooksAdapter
    private lateinit var bookArrayList: ArrayList<Book>
    private lateinit var bookRecyclerView: RecyclerView
    private lateinit var ref: DatabaseReference


//    private val booksService: BooksService
//        get() = (requireActivity().applicationContext as App).booksService

    // This property is only valid between onCreateView and
    // onDestroyView.
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
//        initRecycleView(binding)
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

//    private fun initRecycleView(binding: FragmentBooksBinding) {
//        adapter = BooksAdapter(object : BooksActionListener {
//            override fun onBookClick(book: Book) {
//                val gson = Gson()
//                val intent = Intent(activity, BookActivity::class.java)
//                intent.putExtra("book", gson.toJson(book))
//                startActivity(intent)
//            }
//        })
//        binding.recyclerView1.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        binding.recyclerView1.adapter = adapter
//        binding.recyclerView2.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        binding.recyclerView2.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        binding.recyclerView2.adapter = adapter
//        binding.recyclerView3.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        binding.recyclerView3.adapter = adapter
//        binding.recyclerView4.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        binding.recyclerView4.adapter = adapter
//        binding.recyclerView5.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        binding.recyclerView5.adapter = adapter
//        binding.recyclerView6.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        binding.recyclerView6.adapter = adapter
////        booksService.addListener(booksListener)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
//        booksService.removeListener(booksListener)
    }

//    private val booksListener: BooksListener = {
//        adapter.books = it
//    }
}