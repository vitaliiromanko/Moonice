package com.nulp.moonice.authorization.forgotpassword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nulp.moonice.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var bindingClass : ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
    }
}