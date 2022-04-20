package com.nulp.moonice.utils

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AppValueEventListener(val onSuccess: (DataSnapshot) -> Unit) : ValueEventListener {
    override fun onCancelled(p0: DatabaseError) {
        Log.w("DatabaseError", "loadPost:onCancelled", p0.toException());
    }

    override fun onDataChange(p0: DataSnapshot) {
        onSuccess(p0)
    }

}