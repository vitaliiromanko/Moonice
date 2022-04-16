package com.nulp.moonice.ui.books

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.nulp.moonice.*
import com.nulp.moonice.databinding.FragmentBooksBinding
import com.nulp.moonice.model.Book
import com.nulp.moonice.model.BooksListener
import com.nulp.moonice.model.BooksService

class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private lateinit var adapter: BooksAdapter

    private val booksService: BooksService
        get() = (requireActivity().applicationContext as App).booksService

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(BooksViewModel::class.java)

        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textBooks
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

//        binding.testActivity.setOnClickListener {
//            val intent = Intent(this, PlayerActivity::class.java)
//            // start your next activity
//            startActivity(intent)
//        }

        initRecycleView(binding)
        return root
    }

    private fun initRecycleView(binding: FragmentBooksBinding) {
        adapter = BooksAdapter(object : BooksActionListener {
            override fun onBookClick(book: Book) {
                Log.d("Test", "Clicked")
                //Toast.makeText(this@MainActivity, "Book: ${book.title}", Toast.LENGTH_SHORT).show()
                val gson = Gson()
                val intent = Intent(activity, BookActivity::class.java)
                intent.putExtra("title", gson.toJson(book.title))
                intent.putExtra("author", gson.toJson(book.author))
                intent.putExtra("uploadDate", gson.toJson(book.uploadDate))
                intent.putExtra("description", gson.toJson(book.description))
                intent.putExtra("genre", gson.toJson(book.genre))
                intent.putExtra("audioRecords", gson.toJson(book.audioRecords))
                startActivity(intent)
            }
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter
        booksService.addListener(booksListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        booksService.removeListener(booksListener)
    }

    private val booksListener: BooksListener = {
        adapter.books = it
    }
}