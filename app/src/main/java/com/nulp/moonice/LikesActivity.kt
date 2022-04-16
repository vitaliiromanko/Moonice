package com.nulp.moonice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nulp.moonice.databinding.ActivityBookBinding
import com.nulp.moonice.databinding.ActivityLikesBinding

class LikesActivity : AppCompatActivity() {
    private lateinit var bindingClass : ActivityLikesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityLikesBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
    }
}