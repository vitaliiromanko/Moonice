package com.nulp.moonice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nulp.moonice.databinding.ActivityBookBinding
import com.nulp.moonice.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var bindingClass : ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
    }
}