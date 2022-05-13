package com.nulp.moonice.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nulp.moonice.R
import com.nulp.moonice.authorization.login.LoginActivity
import com.nulp.moonice.databinding.ActivityMainBinding
import com.nulp.moonice.ui.fragment.main.search.SearchFragment
import com.nulp.moonice.utils.*
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference


    private lateinit var navView: NavigationView
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout

    //        theme changer
    private lateinit var switch: SwitchCompat
    private lateinit var appSettingPrefs: SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences.Editor

    // user info in drawer
    private lateinit var headerView: View
    private lateinit var usernameText: TextView
    private lateinit var profilePicture: ImageView

    // search operations
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var searchView: SearchView
    private lateinit var toolbarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
        auth = FirebaseAuth.getInstance()

        drawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.appBarMain.toolbar,
            R.string.app_name, R.string.app_name
        )

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        navView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_books, R.id.nav_bookmarks
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // show user info in drawer
        headerView = navView.getHeaderView(0)
        usernameText = headerView.findViewById(R.id.drawer_username)
        profilePicture = headerView.findViewById(R.id.profile_picture)

        val user = auth.currentUser
        val userId = user?.uid ?: "0"
        val userInfo = ref.child(NODE_USERS).child(NODE_USER_DETAILS)

        userInfo.addValueEventListener(AppValueEventListener {
            usernameText.text = it.child(userId).child(USER_DETAILS_USERNAME).value as String
            val pictureLink = it.child(userId).child(USER_DETAILS_PROFILE_IMAGE).value as String
            if (pictureLink != "") {
                Picasso.get().load(pictureLink)
                    .into(profilePicture)
            }
        })

        // show user info in drawer


        // search
        searchView = binding.appBarMain.searchView
        toolbarTitle = binding.appBarMain.textView

        searchView.setOnSearchClickListener {
            searchView.layoutParams.width = MATCH_PARENT
            toolbarTitle.visibility = View.GONE
            val navHostFragment = R.id.nav_host_fragment_content_main
            toggle.isDrawerIndicatorEnabled = false
            supportFragmentManager.beginTransaction()
                .replace(navHostFragment, SearchFragment(), "Search").commit()

        }

        searchView.setOnCloseListener {
            searchView.layoutParams.width = WRAP_CONTENT
            toolbarTitle.visibility = View.VISIBLE
            toggle.isDrawerIndicatorEnabled = true
            val fragment = supportFragmentManager.findFragmentByTag("Search")
            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }

            return@setOnCloseListener false
        }
        // search

        //theme changer
        appSettingPrefs = getSharedPreferences("AppSettingPrefs", 0)
        sharedPreferences = appSettingPrefs.edit()
        val isDarkThemeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        switch = navView.menu.findItem(R.id.nav_dark_theme).actionView as SwitchCompat

        if (isDarkThemeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            switch.isChecked = true
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            switch.isChecked = false
        }

        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferences.putBoolean("NightMode", true)
                sharedPreferences.apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferences.putBoolean("NightMode", false)
                sharedPreferences.apply()
            }

        }
        //theme changer

        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            auth.signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
            true
        }
        navView.menu.findItem(R.id.nav_edit_profile).setOnMenuItemClickListener {
            startActivity(Intent(this@MainActivity, EditProfileActivity::class.java))
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
            searchView.layoutParams.width = WRAP_CONTENT
            toolbarTitle.visibility = View.VISIBLE
            toggle.isDrawerIndicatorEnabled = true
            val fragment = supportFragmentManager.findFragmentByTag("Search")
            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        } else {
            super.onBackPressed()
        }
    }
}