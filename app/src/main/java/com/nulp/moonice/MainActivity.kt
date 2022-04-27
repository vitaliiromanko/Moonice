package com.nulp.moonice

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.nulp.moonice.authorization.login.LoginActivity
import com.nulp.moonice.databinding.ActivityMainBinding
import com.nulp.moonice.fragment.main_ui.bookmarks.BookmarksFragment
import com.nulp.moonice.fragment.main_ui.books.BooksFragment
import com.nulp.moonice.fragment.main_ui.settings.SettingsFragment


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var navView: NavigationView
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout

    //        theme changer
    private lateinit var switch: SwitchCompat
    private lateinit var appSettingPrefs: SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences.Editor
    //        theme changer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
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

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

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
}