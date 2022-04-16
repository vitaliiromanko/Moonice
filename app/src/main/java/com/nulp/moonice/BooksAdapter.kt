package com.nulp.moonice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nulp.moonice.databinding.ItemBookBinding
import com.nulp.moonice.model.Book

interface BooksActionListener {
    fun onBookClick(book: Book)
}


class BooksAdapter(
    private val actionListener: BooksActionListener
) : RecyclerView.Adapter<BooksAdapter.BooksViewHolder>(), View.OnClickListener {

    var books: List<Book> = emptyList()
        set(newValue) {
            field = newValue
        }

    override fun onClick(v: View) {
        val book = v.tag as Book
        when (v.id) {
            R.id.book_icon -> {
                actionListener.onBookClick(book)
            }
            R.id.test -> {
                actionListener.onBookClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBookBinding.inflate(inflater, parent, false)

//        binding.test.setOnClickListener(this)
        binding.bookIcon.setOnClickListener(this)
        binding.test.setOnClickListener(this)

        return BooksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val book = books[position]
        with(holder.binding) {
            holder.itemView.tag = book
            bookIcon.tag = book
            test.tag = book

            bookGenre.text = book.genre.genreName
            bookTitle.text = book.title
        }
    }

    override fun getItemCount(): Int = books.size

    class BooksViewHolder(
        val binding: ItemBookBinding
    ) : RecyclerView.ViewHolder(binding.root)
}