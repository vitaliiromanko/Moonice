package com.nulp.moonice.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.javafaker.Faker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nulp.moonice.R
import com.nulp.moonice.databinding.ActivityEditProfileBinding
import com.nulp.moonice.utils.*
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var cal: Calendar = Calendar.getInstance()
    private val today: Calendar = Calendar.getInstance()
    private val millieYear: Long = 31556952000
    private var buttonDate: TextInputEditText? = null
    private var textviewDate: TextView? = null
    private lateinit var saveButton: ImageButton
    private lateinit var usernameOld: String
    private lateinit var uploadProfilePicture: ImageView
    private var profilePictureChanged: Boolean = false
    private lateinit var selectedPictureUri: Uri
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference

        setContentView(binding.root)

        textviewDate = binding.birthDateEditProfile
        buttonDate = binding.birthDateEditProfile
        saveButton = binding.saveButton
        uploadProfilePicture = binding.profilePictureCameraEditProfile


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
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: "0"
        val userReference = ref.child(NODE_USERS).child(NODE_USER_DETAILS).child(userId)
        initPage(binding, userReference)

        saveButton.setOnClickListener {
            checkUserInfo(binding, userReference, userId)
        }

        uploadProfilePicture.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(intent, 0)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPictureUri = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPictureUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            binding.profilePictureEditProfile.setImageDrawable(bitmapDrawable)
            profilePictureChanged = true
        }
    }

    private var username: String = ""
    private var email: String = ""
    private var newPassword: String = ""
    private var birthDate: String = ""

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textviewDate!!.text = sdf.format(cal.time)
    }

    private fun initPage(binding: ActivityEditProfileBinding, userRef: DatabaseReference) {

        userRef.addValueEventListener(AppValueEventListener {
            binding.usernameEditProfile.setText(it.child(USER_DETAILS_USERNAME).value as String)
            usernameOld = it.child(USER_DETAILS_USERNAME).value as String
            binding.emailEditProfile.setText(it.child(USER_DETAILS_EMAIL).value as String)
            textviewDate!!.text = it.child(USER_DETAILS_BIRTH_DATE).value as String
            val pictureLink = it.child(USER_DETAILS_PROFILE_IMAGE).value as String
            if (pictureLink != "") {
                Picasso.get().load(pictureLink)
                    .into(binding.profilePictureEditProfile)
            }
        })

        binding.backToMainActivityBtn.setOnClickListener {
            finish()
        }

    }


    private fun checkUserInfo(
        binding: ActivityEditProfileBinding,
        userRef: DatabaseReference,
        uid: String
    ) {
        username = binding.usernameEditProfile.text.toString().trim()
        email = binding.emailEditProfile.text.toString().trim()
        newPassword = binding.newPasswordEditProfile.text.toString().trim()
        birthDate = textviewDate!!.text.toString().trim()
        val cPassword = binding.confirmPasswordEditProfile.text.toString().trim()

        val allUsers = ref.child(NODE_USERS)

        var mistakeCount = 0
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditProfileLayout.error = "Invalid email..."
            mistakeCount++
        }

        if (newPassword.isNotEmpty()) {
            if (cPassword.isEmpty()) {
                binding.confirmPasswordEditProfileLayout.error = "Confirm password..."
                mistakeCount++
            }

            if (newPassword != cPassword) {
                binding.confirmPasswordEditProfileLayout.error = "Password doesn't match..."
                mistakeCount++
            }
        }

        if (birthDate == "--/--/----") {
            binding.birthDateButtonEditProfile.error = "Please enter date of birth..."
            mistakeCount++
        }

        if (username.isEmpty()) {
            binding.usernameEditProfileLayout.error = "Please enter username..."
            mistakeCount++
        } else {
            allUsers.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(username) && it.child(username).value != uid) {
                        binding.usernameEditProfileLayout.error =
                            "The specified user already exists!"
                        mistakeCount++
                    } else if (mistakeCount == 0) {
                        updateUserInfo(userRef, uid)
                    }
                })
        }
    }

    private fun updateUserInfo(
        userRef: DatabaseReference,
        uid: String
    ) {
        var failure = 0
        if (usernameOld != username) {
            var success = 0
            ref.child(NODE_USERS).child(NODE_USERNAMES).child(usernameOld).removeValue()
                .addOnSuccessListener {
                    success++
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed updating user info. ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            userRef.child(USER_DETAILS_USERNAME).setValue(username).addOnSuccessListener {
                success++
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed updating user info. ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            ref.child(NODE_USERS).child(NODE_USERNAMES).child(username).setValue(uid)
                .addOnSuccessListener {
                    success++
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed updating user info. ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            if (success != 3) {
                failure++
            }
        }
        if (auth.currentUser?.email != email) {
            var success = 0
            auth.currentUser?.updateEmail(email)?.addOnSuccessListener {
                success++
            }?.addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed updating user info. ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            userRef.child(USER_DETAILS_EMAIL).setValue(email).addOnSuccessListener {
                success++
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed updating user info. ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (success != 2) {
                failure++
            }
        }

        userRef.child(USER_DETAILS_BIRTH_DATE).setValue(birthDate).addOnFailureListener { e ->
            failure++
            Toast.makeText(
                this,
                "Failed updating user info. ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (newPassword.isNotEmpty()) {
            auth.currentUser?.updatePassword(newPassword)?.addOnFailureListener { e ->
                failure++
                Toast.makeText(
                    this,
                    "Failed updating user info. ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        if (profilePictureChanged) {
            if (!uploadProfilePictureToFirebaseStorage(userRef)) {
                failure++
            }
        }

        if (failure == 0) {
            Toast.makeText(
                this,
                "Account updated successfully...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadProfilePictureToFirebaseStorage(userRef: DatabaseReference): Boolean {
        var success = false
        val faker = Faker.instance()
        val filename = faker.leagueOfLegends().champion() + faker.space().star() + ".jpg"
        storageRef = FirebaseStorage.getInstance().getReference("/UserAvatars/$filename")
        storageRef.putFile(selectedPictureUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                Log.d("ProfilePicUrl", it.toString())
                userRef.child(USER_DETAILS_PROFILE_IMAGE)
                    .setValue(it.toString())
            }
            success = true
        }
        return success
    }
}