package com.nulp.moonice

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nulp.moonice.databinding.ActivityBookBinding
import com.nulp.moonice.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var bindingClass : ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        setSupportActionBar(bindingClass.myToolbar)

        bindingClass.backToMainActivityBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // start your next activity
            startActivity(intent)
        }
    }
}