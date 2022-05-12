package com.nulp.moonice.authorization.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nulp.moonice.R
import com.nulp.moonice.authorization.forgotpassword.ForgotPasswordActivity
import com.nulp.moonice.authorization.register.RegistrationActivity
import com.nulp.moonice.databinding.ActivityLoginBinding
import com.nulp.moonice.ui.MainActivity
import com.nulp.moonice.utils.*
import com.nulp.moonice.vital_changer.LoadingDialog
import java.util.*


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {

        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)

        if (appSettingPrefs.getBoolean("NightMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater).also { setContentView(it.root) }

        ref =
            FirebaseDatabase.getInstance(FIREBASE_URL)
                .getReference(NODE_USERS)

        auth = FirebaseAuth.getInstance()


        checkAuthState()

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account.idToken!!, task)
                }
            } catch (e: ApiException) {
                Log.d("Exception", "Api exception")
            }
        }

        binding.googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        binding.signUpButton.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
            finish()
        }

        binding.forgotPasswordButton.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
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

    private fun checkAuthState() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                auth.signOut()
            }
        }
    }

    private fun getGoogleClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInClient = getGoogleClient()
        launcher.launch(signInClient.signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String, task: Task<GoogleSignInAccount>) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val isNew = it.result.additionalUserInfo!!.isNewUser
                    if (isNew) {
                        updateGoogleUserInfo(task)
                    } else {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Login failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private var email = ""
    private var password = ""

    private fun validateData() {
        email = binding.userEmail.text.toString().trim()
        password = binding.userPassword.text.toString().trim()

        var mistakeCount = 0
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.userEmailLayout.error = "Invalid email..."
            mistakeCount++
        }

        if (password.isEmpty()) {
            binding.userPasswordLayout.error = "Please enter password..."
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
                    "Login failed. ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateGoogleUserInfo(task: Task<GoogleSignInAccount>) {

        val timestamp = System.currentTimeMillis()

        val currentUser = task.result

        val uid = auth.uid

        val hashMap: HashMap<String, Any?> = HashMap()

        hashMap[USER_DETAILS_USERNAME] = currentUser?.displayName
        hashMap[USER_DETAILS_EMAIL] = currentUser.email
        hashMap[USER_DETAILS_BIRTH_DATE] = "--/--/----"
        hashMap[USER_DETAILS_PROFILE_IMAGE] = ""
        hashMap[USER_DETAILS_TIMESTAMP] = timestamp


        ref.child(NODE_USER_DETAILS).child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed saving user info. ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun checkUser() {
        val firebaseUser = auth.currentUser!!

        ref.child(NODE_USER_DETAILS).child(firebaseUser.uid)
            .addListenerForSingleValueEvent(AppValueEventListener {
                if (firebaseUser.isEmailVerified) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Verify the specified mail.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}