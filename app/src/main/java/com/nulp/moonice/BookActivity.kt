package com.nulp.moonice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.nulp.moonice.databinding.ActivityBookBinding
import com.nulp.moonice.model.Book
import java.text.DateFormat
import java.text.SimpleDateFormat

class BookActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)
        val gson = Gson()
        val book = gson.fromJson(intent.getStringExtra("book"), Book::class.java)
        val dateFormat: DateFormat = SimpleDateFormat("yyyy")
        val strDate = dateFormat.format(book.uploadDate)

        binding.bookTitleMain.text = book.title
        binding.navHeaderBook.bookTitleHeaderBar.text = book.title
        binding.navHeaderBook.bookAuthorHeaderBar.text = "${book.author} $strDate"
        binding.navHeaderBook.bookDescriptionHeaderBar.text = book.description
        binding.navHeaderBook.bookGenreHeaderBar.text = book.genre.genreName
        binding.navHeaderBook.navHeaderBookBookImage.setBackgroundResource(book.picturePath)

        binding.activityBookBackButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}