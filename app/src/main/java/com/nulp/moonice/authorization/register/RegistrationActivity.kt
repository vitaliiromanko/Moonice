package com.nulp.moonice.authorization.register

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nulp.moonice.MainActivity
import com.nulp.moonice.authorization.login.LoginActivity
import com.nulp.moonice.databinding.ActivityRegistrationBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap
import kotlin.time.Duration.Companion.milliseconds

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth


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

        textview_date = binding.birthDate
        button_date = binding.birthDateButton


        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        button_date!!.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this@RegistrationActivity,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            cal.add(Calendar.YEAR, -130)
            datePickerDialog.datePicker.minDate = cal.timeInMillis
            cal.add(Calendar.YEAR, 128)
            datePickerDialog.datePicker.maxDate = cal.timeInMillis
            cal.add(Calendar.YEAR, -10)
            datePickerDialog.show()
        }
    }
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.time)
    }



    private var username: String = ""
    private var email: String = ""
    private var password: String = ""
    private var birthDate: String = ""
    private var button_date: ImageButton? = null
    private var textview_date: TextView? = null
    var cal = Calendar.getInstance()


    private fun register() {
        username = binding.username.text.toString().trim()
        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()
        birthDate = binding.birthDate.text.toString().trim()
        val cPassword = binding.confirmPassword.text.toString().trim()

        var mistakeCount = 0
        if (username.isEmpty()) {
            binding.username.error = "Please enter username..."
            mistakeCount++
        }

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

        if (mistakeCount == 0) {
            createUserAccount()
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
        hashMap["uid"] = uid
        hashMap["username"] = username
        hashMap["email"] = email
        hashMap["birthDate"] = birthDate
        hashMap["profileImage"] = "" //add empty, will do in profile edit
        hashMap["timestamp"] = timestamp

        val ref =
            FirebaseDatabase.getInstance("https://moonicedatabase-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Users")
        ref.child(uid!!)
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