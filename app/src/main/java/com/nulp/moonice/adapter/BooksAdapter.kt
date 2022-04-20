package com.nulp.moonice.adapter

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
            notifyDataSetChanged()
        }

    override fun onClick(v: View) {
        val book = v.tag as Book
        actionListener.onBookClick(book)
//        when (v.id) {
//            R.id.book_icon -> {
//                actionListener.onBookClick(book)
//            }
//            R.id.book_image_logo -> {
//                actionListener.onBookClick(book)
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBookBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.bookImageLogo.setOnClickListener(this)

        return BooksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val book = books[position]
        with(holder.binding) {
            holder.itemView.tag = book
            bookIcon.tag = book
            bookImageLogo.tag = book

            bookGenre.text = book.genre.genreName
            bookTitle.text = book.title
            //bookImageLogo.setImageResource(book.picturePath)
            bookImageLogo.setImageResource(book.picturePath)
        }
    }

    override fun getItemCount(): Int = books.size

    class BooksViewHolder(
        val binding: ItemBookBinding
    ) : RecyclerView.ViewHolder(binding.root)
}