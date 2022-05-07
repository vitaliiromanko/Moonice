package com.nulp.moonice.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nulp.moonice.databinding.ActivityLikesBinding

class LikesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLikesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}