package com.nulp.moonice.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nulp.moonice.R
import com.nulp.moonice.databinding.ItemBookBinding
import com.nulp.moonice.model.Book
import com.nulp.moonice.utils.FIREBASE_URL
import com.nulp.moonice.utils.NODE_BOOKS
import com.nulp.moonice.utils.NODE_GENRE

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
        val genreKey = book.genre
        val ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
        with(holder.binding) {
            holder.itemView.tag = book
            bookIcon.tag = book
            bookImageLogo.tag = book

//            bookGenre.text = genreValue
            ref.child(NODE_BOOKS).child(NODE_GENRE).child(genreKey.toString()).addValueEventListener( object  : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bookGenre.text = snapshot.value as String
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("errorBookGenre", "couldn't retrieve genre from DB")
                }

            })
            bookTitle.text = book.title
            //bookImageLogo.setImageResource(book.picturePath)
//            bookImageLogo.setImageURI(Uri.parse(book.pictureLink))
            Glide.with(bookIcon)
                .load("http://via.placeholder.com/300.png")
                .into(bookImageLogo)
        }
    }

    override fun getItemCount(): Int = books.size

    class BooksViewHolder(
        val binding: ItemBookBinding
    ) : RecyclerView.ViewHolder(binding.root)
}