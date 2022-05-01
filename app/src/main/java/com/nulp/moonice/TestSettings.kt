package com.nulp.moonice

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class TestSettings : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}