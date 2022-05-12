package com.nulp.moonice.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nulp.moonice.R
import com.nulp.moonice.adapter.LikesAdapter
import com.nulp.moonice.databinding.ActivityLikesBinding
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
        binding = ActivityLikesBinding.inflate(layoutInflater).also { setContentView(it.root) }
        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference


        likeRecyclerView = binding.likeRecyclerView
        likeRecyclerView.layoutManager = LinearLayoutManager(this)
        likeArrayList = arrayListOf()

        likeRecordId = intent.getStringExtra("recordId").toString().trim()
        initRecycleView(likeRecyclerView)

        binding.backToPlayerActivityButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        drawLayout()

        val tryAgainButton = findViewById<Button>(R.id.fragment_lost_network_try_again_button)
        tryAgainButton.setOnClickListener{
            drawLayout()
        }
    }

    private fun drawLayout() {
        if (NetworkHelper.isNetworkConnected(this)) {
            binding.fragmentLostNetwork.root.visibility = View.GONE
            binding.likeRecyclerView.visibility = View.VISIBLE
        } else {
            binding.fragmentLostNetwork.root.visibility = View.VISIBLE
            binding.likeRecyclerView.visibility = View.GONE
        }
    }

    private fun initRecycleView(recyclerView: RecyclerView) {
        ref.addValueEventListener(AppValueEventListener {
            if (it.child(NODE_BOOKS).child(NODE_RECORDS_LIKES).child(likeRecordId).exists()) {
                likeArrayList.clear()
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

}