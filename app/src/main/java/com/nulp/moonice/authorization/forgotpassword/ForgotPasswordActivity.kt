package com.nulp.moonice.authorization.forgotpassword

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nulp.moonice.authorization.login.LoginActivity
import com.nulp.moonice.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.activityForgotPasswordBackToLogin.setOnClickListener {
            startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
            finish()
        }

        binding.activityForgotPasswordReset.setOnClickListener {
            validateData()
        }
    }

    private var email = ""

    private fun validateData() {
        email = binding.activityForgotPasswordEmail.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.activityForgotPasswordEmail.error = "Invalid email..."
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Email sent successfully to reset your password!",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(
                            Intent(
                                this@ForgotPasswordActivity,
                                LoginActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "An error has occurred! Message not sent!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}