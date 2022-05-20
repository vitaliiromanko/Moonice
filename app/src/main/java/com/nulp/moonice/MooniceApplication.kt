package com.nulp.moonice

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.nulp.moonice.utils.FIREBASE_URL

class MooniceApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance(FIREBASE_URL).setPersistenceEnabled(true)
    }
}