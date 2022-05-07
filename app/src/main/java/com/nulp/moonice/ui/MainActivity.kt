package com.nulp.moonice.ui

import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
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
import com.google.firebase.database.*
import com.nulp.moonice.R
import com.nulp.moonice.authorization.login.LoginActivity
import com.nulp.moonice.databinding.ActivityMainBinding
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
    //        theme changer

    private lateinit var headerView: View
    private lateinit var usernameText: TextView

    private lateinit var searchView: SearchView
    private lateinit var appBarMainTitleLayout: LinearLayout
    private lateinit var listViewAppBarMain: ListView
    private lateinit var listAdapter: ArrayAdapter<String>
    private lateinit var itemNotFound: TextView
    private lateinit var searchList: View
    private lateinit var contentMain: View
    private lateinit var profilePicture: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
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

        headerView = navView.getHeaderView(0)
        usernameText = headerView.findViewById(R.id.drawer_username)
        profilePicture = headerView.findViewById(R.id.profile_picture)

        val user = auth.currentUser
        val userId = user?.uid ?: "0"
//        usernameText.text = ref.child("Users").child("UsersInfo").
//        child(userId).child("username").toString()
        val userInfo = ref.child(NODE_USERS).child(NODE_USER_DETAILS)

        userInfo.addValueEventListener(AppValueEventListener {
            usernameText.text = it.child(userId).child(USER_DETAILS_USERNAME).value as String
            val pictureLink = it.child(userId).child(USER_DETAILS_PROFILE_IMAGE).value as String
            if (pictureLink != "") {
                Picasso.get().load(pictureLink)
                    .into(profilePicture)
            }
        })

//        username.addOnSuccessListener {
//            usernameText.text = "${it.value}"
//        }.addOnFailureListener{
//            usernameText.text = "Vita"
//        }
//        username.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val value = dataSnapshot.getValue(String::class.java)
//                usernameText.text = value
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                usernameText.text = "Anonymous"
//            }
//        })
//        if (user != null) {
//            usernameText.text =
//        } else {
//            usernameText.text = "Anonymous"
//        }
        searchView = binding.appBarMain.searchView
        appBarMainTitleLayout = binding.appBarMain.fab
        listViewAppBarMain = findViewById(R.id.list_view_app_bar_main)
        val books = arrayOf(
            "Nikita running on my beach", "Vitalya rainy dancing",
            "Boss of the gym", "My yaoi friend"
        )
        listAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, books)
        listViewAppBarMain.adapter = listAdapter
        itemNotFound = findViewById(R.id.item_not_found)
        searchList = findViewById(R.id.search_list)
        contentMain = findViewById(R.id.content_main)

        searchView.setOnSearchClickListener {
            itemNotFound.visibility = View.GONE
            appBarMainTitleLayout.visibility = View.GONE
            searchView.layoutParams.width = MATCH_PARENT
            searchList.visibility = View.VISIBLE
            contentMain.visibility = View.GONE

        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                itemNotFound.visibility = View.GONE
                searchView.clearFocus()
                if (books.contains(query)) {
                    listAdapter.filter.filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listAdapter.filter.filter(newText)
                return false
            }

        })

        searchView.setOnCloseListener {
            itemNotFound.visibility = View.GONE
            appBarMainTitleLayout.visibility = View.VISIBLE
            searchView.layoutParams.width = WRAP_CONTENT
            searchList.visibility = View.GONE
            contentMain.visibility = View.VISIBLE

            return@setOnCloseListener false
        }

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
}