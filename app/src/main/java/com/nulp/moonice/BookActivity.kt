package com.nulp.moonice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.nulp.moonice.databinding.ActivityBookBinding
import com.nulp.moonice.model.Genre
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class BookActivity : AppCompatActivity() {

    private lateinit var bindingClass : ActivityBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityBookBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        setSupportActionBar(bindingClass.myToolbar)
        val gson = Gson()
        val title = gson.fromJson(intent.getStringExtra("title"), String::class.java)
        val author = gson.fromJson(intent.getStringExtra("author"), String::class.java)
        val uploadDate = gson.fromJson(intent.getStringExtra("uploadDate"), Date::class.java)
        val description = gson.fromJson(intent.getStringExtra("description"), String()::class.java)
        val genre = gson.fromJson(intent.getStringExtra("genre"), Genre::class.java)
        //val genre = gson.fromJson(intent.getStringExtra("genre"), Genre::class.java)
        val dateFormat: DateFormat = SimpleDateFormat("yyyy")
        val strDate = dateFormat.format(uploadDate)

        bindingClass.bookTitleMain.text = title
        bindingClass.navHeaderBook.bookTitleHeaderBar.text = title
        bindingClass.navHeaderBook.bookAuthorHeaderBar.text = "$author $strDate"
        bindingClass.navHeaderBook.bookDescriptionHeaderBar.text = description
        bindingClass.navHeaderBook.bookGenreHeaderBar.text = genre.genreName

    }
}