package com.nulp.moonice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.nulp.moonice.databinding.ItemBookBinding
import com.nulp.moonice.model.Book
import com.nulp.moonice.utils.AppValueEventListener
import com.nulp.moonice.utils.FIREBASE_URL
import com.nulp.moonice.utils.NODE_BOOKS
import com.nulp.moonice.utils.NODE_GENRE
import com.squareup.picasso.Picasso


interface BooksActionListener {
    fun onBookClick(book: Book)
}


class BooksAdapter(
    private val actionListener: BooksActionListener, private val bookList: ArrayList<Book>
) : RecyclerView.Adapter<BooksAdapter.BooksViewHolder>(), View.OnClickListener {

    override fun onClick(v: View) {
        val book = v.tag as Book
        actionListener.onBookClick(book)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBookBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.bookImageLogo.setOnClickListener(this)

        return BooksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
//        val book = books[position]
        val book = bookList[position]
        val genreKey = book.genre
        val ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
        with(holder.binding) {
            holder.itemView.tag = book
            bookIcon.tag = book
            bookImageLogo.tag = book
            ref.child(NODE_BOOKS).child(NODE_GENRE).child(genreKey.toString())
                .addValueEventListener(AppValueEventListener {
                    bookGenre.text = it.value as String
                })
            bookTitle.text = book.title
            Picasso.get().load(book.pictureLink).into(bookImageLogo)
        }
    }

    //    override fun getItemCount(): Int = books.size
    override fun getItemCount(): Int = bookList.size


    class BooksViewHolder(
        val binding: ItemBookBinding
    ) : RecyclerView.ViewHolder(binding.root)
}