package com.nulp.moonice.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nulp.moonice.databinding.ItemBookmarksBinding
import com.nulp.moonice.model.Bookmark
import com.squareup.picasso.Picasso


interface BookmarkActionListener {
    fun onBookmarkClick(bookmark: Bookmark)
}

class BookmarkAdapter(
    private val actionListener: BookmarkActionListener,
    private val bookmarkList: List<Bookmark>
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>(), View.OnClickListener {

    override fun onClick(v: View) {
        val bookmark = v.tag as Bookmark
        Log.d("Test", "On click bookmark.")
        actionListener.onBookmarkClick(bookmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBookmarksBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.bookmarkImageLogo.setOnClickListener(this)

        return BookmarkViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val bookmark = bookmarkList[position]
        with(holder.binding) {
            holder.itemView.tag = bookmark
            bookmarkImageLogo.tag = bookmark
            bookmarkItemTitle.text = bookmark.book!!.title
            bookmarkItemPublishData.text = "${bookmark.book!!.author}, ${bookmark.book!!.publishDate}"
            bookmarkItemChapterData.text = "Ch.${bookmark.record!!.chapterNumber} ${bookmark.record!!.chapterTitle}"
            Picasso.get().load(bookmark.book!!.pictureLink).into(bookmarkImageLogo)
        }
    }

    override fun getItemCount(): Int = bookmarkList.size


    class BookmarkViewHolder(
        val binding: ItemBookmarksBinding
    ) : RecyclerView.ViewHolder(binding.root)
}