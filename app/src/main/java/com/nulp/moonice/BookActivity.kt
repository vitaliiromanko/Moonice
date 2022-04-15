package com.nulp.moonice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nulp.moonice.databinding.ActivityBookBinding
import com.nulp.moonice.databinding.ActivityLoginBinding

class BookActivity : AppCompatActivity() {

    private lateinit var bindingClass : ActivityBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityBookBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        setSupportActionBar(bindingClass.myToolbar)
    }
}