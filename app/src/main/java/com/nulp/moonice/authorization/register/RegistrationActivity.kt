package com.nulp.moonice.authorization.register

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.View.OnFocusChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nulp.moonice.R
import com.nulp.moonice.authorization.login.LoginActivity
import com.nulp.moonice.databinding.ActivityRegistrationBinding
import com.nulp.moonice.utils.*
import com.nulp.moonice.vital_changer.LoadingDialog
import java.text.SimpleDateFormat
import java.util.*


class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private var cal: Calendar = Calendar.getInstance()
    private val today: Calendar = Calendar.getInstance()
    private val millieYear: Long = 31556952000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityRegistrationBinding.inflate(layoutInflater).also { setContentView(it.root) }

        auth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            val loading = LoadingDialog(this)
            loading.startLoading()
            val handler = Handler()
            handler.postDelayed({
                Log.d("Load", "Ing")
                loading.isDismiss()
                register()
                Log.d("Register", "User")
            }, 3000)
        }

        binding.backToLogin.setOnClickListener {
            startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
            finish()
        }

        textviewDate = binding.birthDate
        buttonDate = binding.birthDate


        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                updateDateInView()
            }

        buttonDate!!.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val datePickerDialog = DatePickerDialog(
                    this@RegistrationActivity, R.style.DatePickerDialogTheme,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )
                Log.d("Test", cal.timeInMillis.toString())
                datePickerDialog.datePicker.minDate = today.timeInMillis - 130 * millieYear
                datePickerDialog.datePicker.maxDate = today.timeInMillis - 2 * millieYear
                datePickerDialog.show()
            }
        }
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textviewDate!!.text = sdf.format(cal.time)
    }


    private var username: String = ""
    private var email: String = ""
    private var password: String = ""
    private var birthDate: String = ""
    private var buttonDate: TextInputEditText? = null
    private var textviewDate: TextView? = null


    private fun register() {
        username = binding.username.text.toString().trim()
        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()
        birthDate = textviewDate!!.text.toString().trim()
        Log.d("BirthDate", birthDate)
        val cPassword = binding.confirmPassword.text.toString().trim()

        ref =
            FirebaseDatabase.getInstance(FIREBASE_URL)
                .getReference(NODE_USERS)

        var mistakeCount = 0
        if (username.isEmpty()) {
            binding.usernameLayout.error = "Please enter username..."
            mistakeCount++
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Invalid email..."
            mistakeCount++
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = "Please enter password..."
            mistakeCount++
        }

        if (cPassword.isEmpty()) {
            binding.confirmPasswordLayout.error = "Confirm password..."
            mistakeCount++
        }

        if (password != cPassword) {
            binding.confirmPassword.error = "Password doesn't match..."
            mistakeCount++
        }

        if (birthDate == "--/--/----") {
            binding.birthDateButton.error = "Please enter date of birth..."
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
                    "Failed creating account. ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateUserInfo() {
        val timestamp = System.currentTimeMillis()

        val uid = auth.uid
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap[USER_DETAILS_USERNAME] = username
        hashMap[USER_DETAILS_EMAIL] = email
        hashMap[USER_DETAILS_BIRTH_DATE] = birthDate
        hashMap[USER_DETAILS_PROFILE_IMAGE] = "" //add empty, will do in profile edit
        hashMap[USER_DETAILS_TIMESTAMP] = timestamp


        ref.child(NODE_USER_DETAILS).child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {

                auth.currentUser!!.sendEmailVerification()
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Account created. Verify the specified mail.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(
                            this@RegistrationActivity,
                            LoginActivity::class.java
                        )
                        intent.putExtra("registration", "validation")
                        startActivity(intent)

                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed send email verification. ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed saving user info. ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    override fun onBackPressed() {
        startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
        finish()
    }
}