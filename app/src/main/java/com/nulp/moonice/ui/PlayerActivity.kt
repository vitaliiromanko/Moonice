package com.nulp.moonice.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.nulp.moonice.R
import com.nulp.moonice.databinding.ActivityPlayerBinding
import com.nulp.moonice.model.AudioRecord
import com.nulp.moonice.utils.*
import com.squareup.picasso.Picasso

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var ref: DatabaseReference
    private lateinit var likeRef: DatabaseReference
    private lateinit var recordRef: DatabaseReference
    private lateinit var user: FirebaseUser
    private lateinit var playButton: ImageButton
    private lateinit var shareButton: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var likeText: TextView

    private lateinit var rotateDiskAnimation: Animation
    private lateinit var diskImage: ImageView

    private var isPlaying: Boolean = false
    private var isLiking: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
        likeRef = ref.child(NODE_BOOKS).child(NODE_RECORDS_LIKES)
        recordRef = ref.child(NODE_BOOKS).child(NODE_RECORDS)
        user = FirebaseAuth.getInstance().currentUser!!
        setContentView(binding.root)
        playButton = findViewById(R.id.playStop)
        shareButton = findViewById(R.id.activity_player_share)
        likeButton = findViewById(R.id.like)
        likeText = findViewById(R.id.like_text)
        setSupportActionBar(binding.myToolbar)

        val gson = Gson()
        val record = gson.fromJson(intent.getStringExtra("record"), AudioRecord::class.java)

        initPage(binding, record)

        diskImage = findViewById(R.id.disk)

        playButton.setOnClickListener {
            if (isPlaying) {
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
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "Hey Check out this Great app: https://vns.lpnu.ua")
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }

        likeButton.setOnClickListener {
            likeRecord(binding, record)
        }

        likeText.setOnClickListener {
            val intent = Intent(this@PlayerActivity, LikesActivity::class.java)
            intent.putExtra("recordId", record.id.toString().trim())
            startActivity(intent)
        }
    }

    private fun initPage(binding: ActivityPlayerBinding, record: AudioRecord) {
        binding.backToBookActivity.setOnClickListener {
            finish()
        }
        val bookId = record.book
        ref.child(NODE_BOOKS).child(NODE_BOOK_DETAILS).child(bookId.toString())
            .addValueEventListener(AppValueEventListener {
                binding.activityPlayerBookTitle.text =
                    it.child(BOOK_DETAILS_TITLE).value as String
                Picasso.get().load(it.child(BOOK_DETAILS_PICTURE_LINK).value as String)
                    .into(binding.activityPlayerBookImage);
            })
        binding.activityPlayerChapterInfo.text =
            "Ch. ${record.chapterNumber} ${record.chapterTitle}"
        binding.likeText.text = record.like.toString()

        likeRef.addValueEventListener(AppValueEventListener {
            if (it.child(record.id.toString()).hasChild(user.uid)) {
                binding.like.setImageResource(R.drawable.ic_liked)
            } else {
                binding.like.setImageResource(R.drawable.ic_like)
            }
        })
    }

    private fun likeRecord(binding: ActivityPlayerBinding, record: AudioRecord) {
        isLiking = true
        likeRef.addValueEventListener(AppValueEventListener {
            if (isLiking) {
                isLiking = if (it.child(record.id.toString()).hasChild(user.uid)) {
                    record.like = record.like?.minus(1)
                    recordRef.child(record.id.toString()).child(RECORD_DETAILS_LIKE).setValue(
                        record.like
                    )
                    likeRef.child(record.id.toString()).child(user.uid).removeValue()
                    binding.like.setImageResource(R.drawable.ic_like)
                    false
                } else {
                    record.like = record.like?.plus(1)
                    recordRef.child(record.id.toString()).child(RECORD_DETAILS_LIKE).setValue(
                        record.like
                    )
                    likeRef.child(record.id.toString()).child(user.uid).setValue("Liked")
                    binding.like.setImageResource(R.drawable.ic_liked)
                    false
                }
                binding.likeText.text = record.like.toString()

            }
        })
    }

    private fun rotateDiskAnimation() {
        rotateDiskAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_disk)
        diskImage.startAnimation(rotateDiskAnimation)
    }
}