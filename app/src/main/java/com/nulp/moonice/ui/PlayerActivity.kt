package com.nulp.moonice.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.nulp.moonice.R
import com.nulp.moonice.databinding.ActivityPlayerBinding
import com.nulp.moonice.model.AudioRecord
import com.nulp.moonice.utils.*
import com.squareup.picasso.Picasso
import java.util.*

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var ref: DatabaseReference
    private lateinit var bookmarkRef: DatabaseReference
    private lateinit var likeRef: DatabaseReference
    private lateinit var recordRef: DatabaseReference
    private lateinit var user: FirebaseUser

    private lateinit var playButton: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var endTime: TextView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var handler: Handler

    private lateinit var shareButton: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var likeText: TextView

    private lateinit var thisRecord: AudioRecord

    private lateinit var rotateDiskAnimation: Animation
    private lateinit var diskImage: ImageView

    private var isLiking: Boolean = false

    private var hasBookmark = false
    private lateinit var userBookmarks: DataSnapshot
    private lateinit var records: DataSnapshot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater).also { setContentView(it.root) }

        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
        bookmarkRef = ref.child(NODE_USERS).child(NODE_BOOKMARKS)
        likeRef = ref.child(NODE_BOOKS).child(NODE_RECORDS_LIKES)
        recordRef = ref.child(NODE_BOOKS).child(NODE_RECORDS)
        user = FirebaseAuth.getInstance().currentUser!!

        playButton = binding.playStop
        seekBar = binding.seekBar
        seekBar.max = 100
        currentTime = binding.currentTime
        endTime = binding.finishTime
        mediaPlayer = MediaPlayer()
        handler = Handler()

        diskImage = binding.disk
        shareButton = binding.activityPlayerShare
        likeButton = binding.like
        likeText = binding.likeText
        setSupportActionBar(binding.myToolbar)

        recordRef.addValueEventListener(AppValueEventListener {
            records = it
        })

        val gson = Gson()
        thisRecord = gson.fromJson(intent.getStringExtra("record"), AudioRecord::class.java)

        initPage(binding)

        binding.bookmark.setOnClickListener {
            bookmark(binding.bookmark)
        }


        playButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                handler.removeCallbacks(updater)
                mediaPlayer.pause()
                playButton.setImageResource(R.drawable.ic_play)
                diskImage.clearAnimation()
            } else {
                mediaPlayer.start()
                updateSeekBar()
                playButton.setImageResource(R.drawable.ic_pause)
                rotateDiskAnimation()
            }
        }

        prepareMediaPlayer()


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val playPosition = (mediaPlayer.duration / 100) * p1
                mediaPlayer.seekTo(playPosition)
                currentTime.text = millisecondsToTimer(mediaPlayer.currentPosition)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                val playPosition = (mediaPlayer.duration / 100) * p0?.progress!!
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                val playPosition = (mediaPlayer.duration / 100) * p0?.progress!!
            }
        })

        shareButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "Hey Check out this Great app: https://vns.lpnu.ua")
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }

        likeButton.setOnClickListener {
            likeRecord(binding)
        }

        likeText.setOnClickListener {
            val intent = Intent(this@PlayerActivity, LikesActivity::class.java)
            intent.putExtra("recordId", thisRecord.id.toString().trim())
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        drawLayout()

        val tryAgainButton = findViewById<Button>(R.id.fragment_lost_network_try_again_button)
        tryAgainButton.setOnClickListener {
            drawLayout()
        }
    }

    private fun drawLayout() {
        if (NetworkHelper.isNetworkConnected(this)) {
            binding.fragmentLostNetwork.root.visibility = View.GONE
            binding.logoFrame.visibility = View.VISIBLE
            binding.bottomToolbar.visibility = View.VISIBLE
        } else {
            binding.fragmentLostNetwork.root.visibility = View.VISIBLE
            binding.logoFrame.visibility = View.GONE
            binding.bottomToolbar.visibility = View.GONE
        }
    }

    private fun bookmark(bookmark: ImageButton) {
        Log.d("Test", "Space")
        Log.d("Test", "First Has bookmark: $hasBookmark")

        if (hasBookmark) {
            for (markSnapshot in userBookmarks.children) {
                val mark = markSnapshot.getValue(Long::class.java)
                if (mark!! == thisRecord.id) {
                    bookmarkRef.child(user.uid)
                        .child(markSnapshot.key.toString()).removeValue()
                    bookmark.setImageResource(R.drawable.ic_bookmark_border)
                    hasBookmark = false
                    Log.d("Test", "Second Has bookmark: $hasBookmark")
                    break
                }
            }
        } else {
            for (recordSnapshot in records.children) {
                for (markSnapshot in userBookmarks.children) {
                    val mark = markSnapshot.getValue(Long::class.java)
                    val nRecord = records.child(mark!!.toString())
                        .getValue(AudioRecord::class.java)
                    nRecord!!.id = recordSnapshot.key?.toLong()
                    if (nRecord.book == thisRecord.book) {
                        bookmarkRef.child(user.uid)
                            .child(markSnapshot.key.toString()).removeValue()
                        break
                    }
                }
            }

            bookmarkRef.child(user.uid)
                .child(Date().time.toString())
                .setValue(thisRecord.id)
            Log.d("Test", "Third Has bookmark: $hasBookmark")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initPage(binding: ActivityPlayerBinding) {
        binding.backToBookActivity.setOnClickListener {
            finish()
        }
        val bookId = thisRecord.book
        ref.child(NODE_BOOKS).child(NODE_BOOK_DETAILS).child(bookId.toString())
            .addValueEventListener(AppValueEventListener {
                binding.activityPlayerBookTitle.text =
                    it.child(BOOK_DETAILS_TITLE).value as String
                Picasso.get().load(it.child(BOOK_DETAILS_PICTURE_LINK).value as String)
                    .into(binding.activityPlayerBookImage)
            })
        binding.activityPlayerChapterInfo.text =
            "Ch. ${thisRecord.chapterNumber} ${thisRecord.chapterTitle}"
        binding.likeText.text = thisRecord.like.toString()

        likeRef.addValueEventListener(AppValueEventListener {
            if (it.child(thisRecord.id.toString()).hasChild(user.uid)) {
                binding.like.setImageResource(R.drawable.ic_liked)
                binding.likeText.typeface = Typeface.DEFAULT_BOLD
            } else {
                binding.like.setImageResource(R.drawable.ic_like)
                binding.likeText.typeface = Typeface.DEFAULT
            }
        })

        bookmarkRef.child(user.uid)
            .addValueEventListener(AppValueEventListener {
                userBookmarks = it
                if (it.exists()) {
                    for (markSnapshot in it.children) {
                        val mark = markSnapshot.getValue(Long::class.java)
                        if (mark!! == thisRecord.id) {
                            binding.bookmark.setImageResource(R.drawable.ic_bookmark)
                            hasBookmark = true
                            break
                        }
                    }
                }
            })
    }

    private fun likeRecord(binding: ActivityPlayerBinding) {
        isLiking = true
        likeRef.addValueEventListener(AppValueEventListener {
            if (isLiking) {
                isLiking = if (it.child(thisRecord.id.toString()).hasChild(user.uid)) {
                    thisRecord.like = thisRecord.like?.minus(1)
                    recordRef.child(thisRecord.id.toString()).child(RECORD_DETAILS_LIKE).setValue(
                        thisRecord.like
                    )
                    likeRef.child(thisRecord.id.toString()).child(user.uid).removeValue()
                    binding.like.setImageResource(R.drawable.ic_like)
                    binding.likeText.typeface = Typeface.DEFAULT
                    false
                } else {
                    thisRecord.like = thisRecord.like?.plus(1)
                    recordRef.child(thisRecord.id.toString()).child(RECORD_DETAILS_LIKE).setValue(
                        thisRecord.like
                    )
                    likeRef.child(thisRecord.id.toString()).child(user.uid).setValue("Liked")
                    binding.like.setImageResource(R.drawable.ic_liked)
                    binding.likeText.typeface = Typeface.DEFAULT_BOLD
                    false
                }
                binding.likeText.text = thisRecord.like.toString()

            }
        })
    }

    private fun rotateDiskAnimation() {
        rotateDiskAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_disk)
        diskImage.startAnimation(rotateDiskAnimation)
    }

    private val updater = object : TimerTask() {
        override fun run() {
            updateSeekBar()
            val currentDuration = mediaPlayer.currentPosition
            currentTime.text = millisecondsToTimer(currentDuration)
        }
    }

    private fun updateSeekBar() {
        if (mediaPlayer.isPlaying) {
            seekBar.progress =
                ((mediaPlayer.currentPosition / mediaPlayer.duration).toFloat() * 100).toInt()
            handler.postDelayed(updater, 500)
        }
    }

    private fun millisecondsToTimer(milliseconds: Int): String {
        var timerString = ""
        var secondsString = ""
        val hours = milliseconds / (1000 * 60 * 60)
        val minutes = milliseconds % (1000 * 60 * 60) / (1000 * 60)
        val seconds = milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000

        if (hours > 0) {
            timerString = "$hours:"
        }

        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "$seconds"
        }

        timerString = "$timerString$minutes:$secondsString"
        return timerString
    }

    private fun prepareMediaPlayer() {
        try {
            mediaPlayer.setDataSource(thisRecord.recordLink)
            mediaPlayer.prepare()
            endTime.text = millisecondsToTimer(mediaPlayer.duration)
        } catch (ex: Throwable) {
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        }
    }
}