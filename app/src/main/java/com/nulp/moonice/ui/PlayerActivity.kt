package com.nulp.moonice.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.nulp.moonice.App
import com.nulp.moonice.R
import com.nulp.moonice.adapter.AudioRecordsActionListener
import com.nulp.moonice.adapter.AudioRecordsAdapter
import com.nulp.moonice.databinding.ActivityBookBinding
import com.nulp.moonice.databinding.ActivityPlayerBinding
import com.nulp.moonice.model.AudioRecord
import com.nulp.moonice.model.Book
import com.nulp.moonice.service.AudioRecordsListener
import com.nulp.moonice.service.AudioRecordsService
import com.nulp.moonice.utils.*
import com.squareup.picasso.Picasso

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var playButton : ImageButton
    private lateinit var shareButton : ImageButton

    private lateinit var rotateDiskAnimation: Animation
    private lateinit var diskImage : ImageView

    private var isPlaying : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playButton = findViewById(R.id.playStop)
        shareButton = findViewById(R.id.activity_player_share)
        setSupportActionBar(binding.myToolbar)

        val gson = Gson()
        val record = gson.fromJson(intent.getStringExtra("record"), AudioRecord::class.java)

        initPage(binding, record)

        diskImage = findViewById(R.id.disk)

        playButton.setOnClickListener {
            if(isPlaying) {
                isPlaying = false
                playButton.setImageResource(R.drawable.ic_play)
                diskImage.clearAnimation()
            } else {
                isPlaying = true
                playButton.setImageResource(R.drawable.ic_pause)
                rotateDiskAnimation()
            }
        }

        shareButton.setOnClickListener {
            val intent= Intent()
            intent.action=Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,"Hey Check out this Great app: https://vns.lpnu.ua")
            intent.type="text/plain"
            startActivity(Intent.createChooser(intent,"Share To:"))
        }

    }

    private fun initPage(binding: ActivityPlayerBinding, record: AudioRecord) {
        binding.backToBookActivity.setOnClickListener {
            finish()
        }
//vitalyaaaa
        val bookId = record.book
        val ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
        ref.child(NODE_BOOKS).child(NODE_BOOK_DETAILS).child(bookId.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.activityPlayerBookTitle.text = snapshot.child(BOOK_DETAILS_TITLE).value as String
                    Picasso.get().load(snapshot.child(BOOK_DETAILS_PICTURE_LINK).value as String).into(binding.activityPlayerBookImage);
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("errorBookGenre", "couldn't retrieve genre from DB")
                }

            })
        binding.activityPlayerChapterInfo.text =
            "Ch. ${record.chapterNumber} ${record.chapterTitle}"
        binding.activityPlayerLike.text = record.like.toString()
    }

    private fun rotateDiskAnimation() {
        rotateDiskAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_disk)
        diskImage.startAnimation(rotateDiskAnimation)
    }
}