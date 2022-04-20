package com.nulp.moonice.authorization.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nulp.moonice.MainActivity
import com.nulp.moonice.authorization.login.LoginActivity
import com.nulp.moonice.databinding.ActivityRegistrationBinding
import com.nulp.moonice.utils.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            register()
        }

        binding.backToLogin.setOnClickListener {
            startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
            finish()
        }
    }


    private var username: String = ""
    private var email: String = ""
    private var password: String = ""
    private var birthDate: String = ""

    private fun register() {
        username = binding.username.text.toString().trim()
        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()
        birthDate = binding.birthDate.text.toString().trim()
        val cPassword = binding.confirmPassword.text.toString().trim()

        ref =
            FirebaseDatabase.getInstance(FIREBASE_URL)
                .getReference(NODE_USERS)

        var mistakeCount = 0

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error = "Invalid email..."
            mistakeCount++
        }

        if (password.isEmpty()) {
            binding.password.error = "Please enter password..."
            mistakeCount++
        }

        if (cPassword.isEmpty()) {
            binding.confirmPassword.error = "Confirm password..."
            mistakeCount++
        }

        if (password != cPassword) {
            binding.confirmPassword.error = "Password doesn't match..."
            mistakeCount++
        }

        if (birthDate.isEmpty()) {
            binding.birthDate.error = "Please enter date of birth..."
            mistakeCount++
        }

        if (username.isEmpty()) {
            binding.username.error = "Please enter username..."
            mistakeCount++
        } else {
            ref.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(username)) {
                        binding.username.error = "The specified user already exists!"
                        mistakeCount++
                        Log.d("Test", "${mistakeCount.toString()} перший")
                    } else if (mistakeCount == 0) {
                        Log.d("Test", "createUserAccount")
                        createUserAccount()
                    }
                })
        }
    }

    private fun createUserAccount() {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed creating account due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateUserInfo() {

        val timestamp = System.currentTimeMillis()

        val uid = auth.uid
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap[USER_DETAILS_UID] = uid
        hashMap[USER_DETAILS_USERNAME] = username
        hashMap[USER_DETAILS_EMAIL] = email
        hashMap[USER_DETAILS_BIRTH_DATE] = birthDate
        hashMap[USER_DETAILS_PROFILE_IMAGE] = "" //add empty, will do in profile edit
        hashMap[USER_DETAILS_TIMESTAMP] = timestamp


        ref.child(NODE_USERNAMES).child(username).setValue(uid)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.child(NODE_USER_DETAILS).child(uid!!)
                        .setValue(hashMap)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Account created...",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Failed saving user info due to ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
    }
}