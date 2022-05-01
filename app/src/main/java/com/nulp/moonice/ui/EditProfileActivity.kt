package com.nulp.moonice.ui

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.nulp.moonice.R
import com.nulp.moonice.databinding.ActivityEditProfileBinding
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditProfileBinding
    private var cal: Calendar = Calendar.getInstance()
    private val today: Calendar = Calendar.getInstance()
    private val millieYear: Long = 31556952000
    private var birthDate: String = ""
    private var buttonDate: TextInputEditText? = null
    private var textviewDate: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textviewDate = binding.birthDateEditProfile
        buttonDate = binding.birthDateEditProfile


        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                updateDateInView()
            }

        buttonDate!!.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val datePickerDialog = DatePickerDialog(
                    this@EditProfileActivity, R.style.DatePickerDialogTheme,
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
}