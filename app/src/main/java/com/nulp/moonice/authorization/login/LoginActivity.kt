package com.nulp.moonice.authorization.login

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mifmif.common.regex.Main
import com.nulp.moonice.MainActivity
import com.nulp.moonice.R
import com.nulp.moonice.authorization.forgotpassword.ForgotPasswordActivity
import com.nulp.moonice.authorization.register.RegistrationActivity
import com.nulp.moonice.databinding.ActivityLoginBinding
import com.nulp.moonice.databinding.ActivityMainBinding
import com.nulp.moonice.utils.AppValueEventListener
import com.nulp.moonice.utils.FIREBASE_URL
import com.nulp.moonice.utils.NODE_USERS
import com.nulp.moonice.utils.NODE_USER_DETAILS
import com.nulp.moonice.vital_changer.LoadingDialog
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlin.concurrent.thread
import android.os.Handler as Handler


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)

        if (appSettingPrefs.getBoolean("NightMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

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
            val loading = LoadingDialog(this)
            loading.startLoading()
            validateData()
            Log.d("Log", "In")
            val handler = Handler()
            handler.postDelayed({
                loading.isDismiss()
                Log.d("Loading", "Stopped")
            }, 1000)

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
            FirebaseDatabase.getInstance(FIREBASE_URL)
                .getReference(NODE_USERS)

        ref.child(NODE_USER_DETAILS).child(firebaseUser.uid)
            .addListenerForSingleValueEvent(AppValueEventListener {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            })
    }
}