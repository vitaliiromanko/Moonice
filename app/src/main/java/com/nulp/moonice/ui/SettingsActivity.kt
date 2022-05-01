package com.nulp.moonice.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nulp.moonice.databinding.ActivityBookBinding
import com.nulp.moonice.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}