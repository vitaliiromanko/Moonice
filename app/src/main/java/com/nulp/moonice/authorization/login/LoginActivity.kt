package com.nulp.moonice.authorization.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nulp.moonice.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var bindingClass : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
    }
}