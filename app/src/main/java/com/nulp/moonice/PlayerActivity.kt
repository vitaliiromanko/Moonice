package com.nulp.moonice

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.nulp.moonice.databinding.ActivityPlayerBinding
import com.nulp.moonice.model.AudioRecord

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var playButton : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playButton = findViewById(R.id.playStop)
        setSupportActionBar(binding.myToolbar)

        val gson = Gson()
        val record = gson.fromJson(intent.getStringExtra("record"), AudioRecord::class.java)

        initPage(binding, record)
        playButton.setOnClickListener {
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun initPage(binding: ActivityPlayerBinding, record: AudioRecord) {
        binding.backToBookActivity.setOnClickListener {
            finish()
        }

        binding.activityPlayerBookTitle.text = record.book.title
        binding.activityPlayerChapterInfo.text =
            "Ch. ${record.chapterNumber} ${record.chapterTitle}"
        binding.activityPlayerBookImage.setImageResource(record.book.picturePath)
        binding.activityPlayerLike.text = record.like.toString()
    }
}