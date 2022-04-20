package com.nulp.moonice.authorization.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nulp.moonice.MainActivity
import com.nulp.moonice.authorization.forgotpassword.ForgotPasswordActivity
import com.nulp.moonice.authorization.register.RegistrationActivity
import com.nulp.moonice.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signUpButton.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
            finish()
        }

        binding.forgotPasswordButton.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
            finish()
        }

        val currentUser = auth.currentUser

        if (currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener {
            validateData()
        }

    }

    private var email = ""
    private var password = ""

    private fun validateData() {
        email = binding.userEmail.text.toString().trim()
        password = binding.userPassword.text.toString().trim()

        var mistakeCount = 0
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.userEmail.error = "Invalid email..."
            mistakeCount++
        }

        if (password.isEmpty()) {
            binding.userPassword.error = "Please enter password..."
            mistakeCount++
        }

        if (mistakeCount == 0) {
            loginUser()
        }
    }

    private fun loginUser() {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUser()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Login failed due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun checkUser() {
        val firebaseUser = auth.currentUser!!
        val ref =
            FirebaseDatabase.getInstance("https://moonicedatabase-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Users")

        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("DatabaseError", "loadPost:onCancelled", error.toException());
                }
            })
    }
}