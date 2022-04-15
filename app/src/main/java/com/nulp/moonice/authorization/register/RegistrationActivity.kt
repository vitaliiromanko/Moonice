package com.nulp.moonice.authorization.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nulp.moonice.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var bindingClass : ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
    }
}