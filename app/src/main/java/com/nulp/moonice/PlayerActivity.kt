package com.nulp.moonice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.nulp.moonice.databinding.ActivityPlayerBinding
import com.nulp.moonice.model.AudioRecord

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)

        val gson = Gson()
        val record = gson.fromJson(intent.getStringExtra("record"), AudioRecord::class.java)

        initPage(binding, record)
    }

    private fun initPage(binding: ActivityPlayerBinding, record: AudioRecord) {
        binding.backToBookActivity.setOnClickListener {
            finish()
        }

        binding.activityPlayerBookTitle.text = record.book.title
        binding.activityPlayerChapterInfo.text =
            "Ch. ${record.chapterNumber} ${record.chapterTitle}"
        binding.activityPlayerBookImage.setBackgroundResource(record.book.picturePath)
        binding.activityPlayerLike.text = record.like.toString()
    }
}