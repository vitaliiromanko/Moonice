package com.nulp.moonice.authorization.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nulp.moonice.MainActivity
import com.nulp.moonice.authorization.forgotpassword.ForgotPasswordActivity
import com.nulp.moonice.authorization.register.RegistrationActivity
import com.nulp.moonice.databinding.ActivityLoginBinding
import com.nulp.moonice.utils.AppValueEventListener
import com.nulp.moonice.utils.FIREBASE_URL
import com.nulp.moonice.utils.NODE_USERS
import com.nulp.moonice.utils.NODE_USER_DETAILS
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlin.concurrent.thread
import android.os.Handler as Handler


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    var progressStatus = 0
    private var i = 0


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


        val progressBar = binding.loading
        binding.loginButton.setOnClickListener {
            progressLoading(progressBar)
            validateData()
        }

    }

    private fun progressLoading(progressBar : ProgressBar){
        progressStatus = 0
        progressBar.progress = progressStatus
        progressBar.visibility = View.VISIBLE
        val thread : Thread = Thread{
            Thread.sleep(100)
        }
        while (progressStatus < 100) {
            // performing some dummy operation
            try {
                thread.start()
                Log.d("Test", progressStatus.toString())
                progressStatus += 10
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            // Updating the progress bar
            progressBar.progress = progressStatus
        }
        progressBar.visibility = View.GONE
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
            FirebaseDatabase.getInstance(FIREBASE_URL)
                .getReference(NODE_USERS)

        ref.child(NODE_USER_DETAILS).child(firebaseUser.uid)
            .addListenerForSingleValueEvent(AppValueEventListener {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            })
    }
}