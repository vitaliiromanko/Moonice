package com.nulp.moonice

import android.app.Application
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nulp.moonice.utils.FIREBASE_URL

class MooniceApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance(FIREBASE_URL).setPersistenceEnabled(true)
    }
}