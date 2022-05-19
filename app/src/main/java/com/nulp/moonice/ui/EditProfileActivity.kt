package com.nulp.moonice.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.FileProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nulp.moonice.R
import com.nulp.moonice.databinding.ActivityEditProfileBinding
import com.nulp.moonice.utils.*
import com.squareup.picasso.Picasso
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var ref: DatabaseReference
    private lateinit var pictureRef: StorageReference
    private lateinit var pictureLink: String
    private lateinit var auth: FirebaseAuth
    private lateinit var curUser: FirebaseUser
    private var cal: Calendar = Calendar.getInstance()
    private val today: Calendar = Calendar.getInstance()
    private val millieYear: Long = 31556952000
    private var buttonDate: TextInputEditText? = null
    private var textviewDate: TextView? = null
    private lateinit var saveButton: ImageButton
    private lateinit var usernameOld: String
    private lateinit var uploadProfilePicture: Button
    private lateinit var takeProfilePicture: ImageView
    private var profilePictureTaken: Boolean = false
    private var profilePictureUploaded: Boolean = false
    private lateinit var capturedPictureUri: Uri
    private lateinit var selectedPictureUri: Uri
    private lateinit var storageRef: StorageReference

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_UPLOAD = 0
    private val PERMISSION_CODE_CAMERA = 1000
    private val PERMISSION_CODE_GALLERY = 1

    private lateinit var photoFile: File
    private lateinit var currentPhotoPath: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityEditProfileBinding.inflate(layoutInflater).also { setContentView(it.root) }

        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference

        textviewDate = binding.birthDateEditProfile
        buttonDate = binding.birthDateEditProfile
        saveButton = binding.saveButton
        takeProfilePicture = binding.profilePictureCameraEditProfile
        uploadProfilePicture = binding.profilePictureUploadEditProfile


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
        curUser = auth.currentUser!!
        val userId = auth.currentUser?.uid ?: "0"
        val userReference = ref.child(NODE_USERS).child(NODE_USER_DETAILS).child(userId)
        initPage(binding, userReference)

        saveButton.setOnClickListener {
            val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
            builder.setMessage("Do you want to save changes to your account?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    checkUserInfo(binding, userReference)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        takeProfilePicture.setOnClickListener {
            // if os is Marshmallow or above we'll ask permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // if we don't have enough permissions to perform a task
                if (checkSelfPermission(android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    val permission = arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    requestPermissions(permission, PERMISSION_CODE_CAMERA)
                }
                // if we have enough permissions, we continue
                else {
                    takePicture()
                }
            }
            // permission is not needed
            else {
                takePicture()
            }
        }

        uploadProfilePicture.setOnClickListener {
            uploadPictureFromGallery()
            // if os is Marshmallow or above we'll ask permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // if we don't have enough permissions to perform a task
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    val permission = arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    requestPermissions(permission, PERMISSION_CODE_GALLERY)
                }
                // if we have enough permissions, we continue
                else {
                    uploadPictureFromGallery()
                }
            }
            // permission is not needed
            else {
                uploadPictureFromGallery()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_UPLOAD && resultCode == Activity.RESULT_OK && data != null) {
            selectedPictureUri = data.data!!
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPictureUri)
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            binding.profilePictureEditProfile.setImageDrawable(bitmapDrawable)
            binding.profilePictureEditProfile.setImageURI(selectedPictureUri)
            profilePictureTaken = false
            profilePictureUploaded = true
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            capturedPictureUri =
                FileProvider.getUriForFile(this, "com.nulp.moonice.fileprovider", photoFile)
            binding.profilePictureEditProfile.setImageURI(capturedPictureUri)
            profilePictureTaken = true
            profilePictureUploaded = false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // executes when user pressed ALLOW or DENY permission
        when (requestCode) {
            // user pressed ALLOW or DENY on CAMERA permission popup
            PERMISSION_CODE_CAMERA -> {
                // if user pressed ALLOW
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture()
                }
                // if user pressed DENY
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                }
            }

            // user pressed ALLOW or DENY on READ_EXTERNAL_STORAGE permission popup
            PERMISSION_CODE_GALLERY -> {
                // if user pressed ALLOW
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadPictureFromGallery()
                }
                // if user pressed DENY
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                }
            }
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
            pictureLink = it.child(USER_DETAILS_PROFILE_IMAGE).value as String
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
    ) {
        username = binding.usernameEditProfile.text.toString().trim()
        email = binding.emailEditProfile.text.toString().trim()
        newPassword = binding.newPasswordEditProfile.text.toString().trim()
        birthDate = textviewDate!!.text.toString().trim()
        val cPassword = binding.confirmPasswordEditProfile.text.toString().trim()


        var mistakeCount = 0
        if (username.isEmpty()) {
            binding.usernameEditProfileLayout.error = "Please enter username..."
            mistakeCount++
        }

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

        if (mistakeCount == 0) {
            updateUserInfo(userRef)
        }
    }

    private fun updateUserInfo(
        userRef: DatabaseReference,
    ) {
        var failure = 0
        userRef.child(USER_DETAILS_USERNAME).setValue(username)
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed updating user info. ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                failure++
            }
        if (auth.currentUser?.email != email) {
            var success = 0
            auth.currentUser?.updateEmail(email)?.addOnSuccessListener {
                success++
                userRef.child(USER_DETAILS_EMAIL).setValue(email).addOnSuccessListener {
                    success++
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed updating user info. ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }?.addOnFailureListener { e ->
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

        if (profilePictureUploaded) {
            if (!uploadProfilePictureToFirebaseStorage(userRef, selectedPictureUri)) {
                failure++
            }
        }

        if (profilePictureTaken) {
            if (!uploadProfilePictureToFirebaseStorage(userRef, capturedPictureUri)) {
                failure++
            }
        }

        if (failure == 0) {
            Toast.makeText(
                this,
                "Account updated successfully...",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun uploadProfilePictureToFirebaseStorage(
        userRef: DatabaseReference,
        pictureUri: Uri
    ): Boolean {
        var success = false
        val filename = curUser.uid + UUID.randomUUID() + ".jpg"
        storageRef = FirebaseStorage.getInstance().getReference("/UserAvatars/$filename")
        storageRef.putFile(pictureUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                Log.d("ProfilePicUrl", it.toString())

                if (pictureLink.isNotEmpty()) {
                    try {
                        FirebaseStorage.getInstance().getReferenceFromUrl(pictureLink).delete()
                    } catch (e: IllegalArgumentException)  {
                        Log.d("Exception", e.toString())
                    }
                }

                userRef.child(USER_DETAILS_PROFILE_IMAGE)
                    .setValue(it.toString())
            }
            success = true
        }
        return success
    }

    private fun uploadPictureFromGallery() {
        val uploadPictureIntent = Intent(Intent.ACTION_PICK)
        uploadPictureIntent.type = "image/*"
        try {
            startActivityForResult(uploadPictureIntent, REQUEST_IMAGE_UPLOAD)
        } catch (e: ActivityNotFoundException) {
            Log.d("Exception", "ActivityNotFoundException")
        }
    }

    private fun takePicture() {
        // function for camera click listener
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()

        // fileprovider changes along with fileprovider in manifest
        val uri = FileProvider.getUriForFile(this, "com.nulp.moonice.fileprovider", photoFile)
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

        // continue to onActivityResult
        try {
            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Log.d("Exception", "ActivityNotFoundException")
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
}