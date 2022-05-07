package com.nulp.moonice.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.gson.Gson
import com.nulp.moonice.adapter.BooksActionListener
import com.nulp.moonice.adapter.BooksAdapter
import com.nulp.moonice.adapter.LikesAdapter
import com.nulp.moonice.databinding.ActivityLikesBinding
import com.nulp.moonice.model.AudioRecord
import com.nulp.moonice.model.Book
import com.nulp.moonice.model.LikeItem
import com.nulp.moonice.utils.*

class LikesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLikesBinding
    private lateinit var ref: DatabaseReference
    private lateinit var likeAdapter: LikesAdapter
    private lateinit var likeArrayList: ArrayList<LikeItem>
    private lateinit var likeRecyclerView: RecyclerView
    private lateinit var likeRecordId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikesBinding.inflate(layoutInflater)
        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference

        setContentView(binding.root)

        likeRecyclerView = binding.likeRecyclerView
        likeRecyclerView.layoutManager = LinearLayoutManager(this)
        likeArrayList = arrayListOf()

        likeRecordId = intent.getStringExtra("recordId").toString().trim()
        initRecycleView(likeRecyclerView)

        binding.backToPlayerActivityButton.setOnClickListener {
            finish()
        }

    }

    private fun initRecycleView(recyclerView: RecyclerView) {
        ref.addValueEventListener(AppValueEventListener {
            if (it.child(NODE_BOOKS).child(NODE_RECORDS_LIKES).child(likeRecordId).exists()) {
                for (likeSnapshot in it.child(NODE_BOOKS).child(NODE_RECORDS_LIKES)
                    .child(likeRecordId).children) {
                    val likeUserID = likeSnapshot.key as String
                    if (it.child(NODE_USERS).child(NODE_USER_DETAILS).child(likeUserID).exists()) {
                        val likeItem =
                            it.child(NODE_USERS).child(NODE_USER_DETAILS).child(likeUserID)
                                .getValue(LikeItem::class.java)!!
                        likeArrayList.add(likeItem)
                        Log.d("LikeActivity.addToList", "${likeItem.username}")
                        Log.d("InLoopSize", "${likeArrayList.size}")
                    }
                }
                Log.d("listArrayList size", "${likeArrayList.size}")
                likeAdapter = LikesAdapter(likeArrayList)
                recyclerView.adapter = likeAdapter

            }
        })
    }

    private fun getLikeItem(userId: String): LikeItem {
        var likeItem = LikeItem()
        var refFinished = false
        ref.child(NODE_USERS).child(NODE_USER_DETAILS).child(userId)
            .get().addOnSuccessListener { snapshot ->
                likeItem = snapshot.getValue(LikeItem::class.java)!!
                Log.d("LikeActivity.addToList", "${likeItem.username}")
                refFinished = true
            }.addOnFailureListener {
                Log.e("firebase", "Error getting likeItem", it)
            }


        return likeItem
    }

}