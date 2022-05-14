package com.nulp.moonice.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
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
import com.nulp.moonice.model.Book
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
    private lateinit var thisRecord: AudioRecord


    // variables for media player
    private lateinit var playButton: ImageButton
    private lateinit var skipBackwardButton: ImageButton
    private lateinit var skipForwardButton: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var endTime: TextView
    private lateinit var mediaPlayer: MediaPlayer
    private var handler: Handler = Handler()
    private val millisecondsFiveSec = 5000
    // variables for media player

    // variables for other functions (share and like)
    private lateinit var book: Book
    private lateinit var bookTitle: TextView
    private lateinit var chapterTitle: TextView
    private lateinit var shareButton: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var likeText: TextView
    private var isLiking: Boolean = false
    // variables for other functions

    // variables for animation
    private lateinit var diskImage: ImageView
    private lateinit var diskImageAnimator: ObjectAnimator
    // variables for animation

    // variables for bookmarks
    private var hasBookmark = false
    private lateinit var userBookmarks: DataSnapshot
    private lateinit var records: DataSnapshot
    // variables for bookmarks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater).also { setContentView(it.root) }

        ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
        bookmarkRef = ref.child(NODE_USERS).child(NODE_BOOKMARKS)
        likeRef = ref.child(NODE_BOOKS).child(NODE_RECORDS_LIKES)
        recordRef = ref.child(NODE_BOOKS).child(NODE_RECORDS)
        user = FirebaseAuth.getInstance().currentUser!!

        bookTitle = binding.activityPlayerBookTitle
        chapterTitle = binding.activityPlayerChapterInfo
        playButton = binding.playStop
        skipBackwardButton = binding.fiveSecAgo
        skipForwardButton = binding.fiveSecForward
        seekBar = binding.seekBar
        seekBar.max = 100
        currentTime = binding.currentTime
        endTime = binding.finishTime
        diskImage = binding.disk
        shareButton = binding.activityPlayerShare
        likeButton = binding.like
        likeText = binding.likeText
        diskImageAnimator = ObjectAnimator.ofFloat(diskImage, View.ROTATION, 360F)
        try {
            mediaPlayer = MediaPlayer()
        } catch (ex: Throwable) {
            finish()
        }


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

        // media player operations

        if (NetworkHelper.isNetworkConnected(this)) {
            prepareMediaPlayer()
        }

        playButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                chapterTitle.isSelected = false
                handler.removeCallbacks(updater)
                mediaPlayer.pause()
                playButton.setImageResource(R.drawable.ic_play)
                diskImageAnimator.pause()
            } else {
                mediaPlayer.start()
                updateSeekBar()
                chapterTitle.isSelected = true
                playButton.setImageResource(R.drawable.ic_pause)
                if (this::diskImageAnimator.isInitialized) {
                    if (diskImageAnimator.isStarted) {
                        diskImageAnimator.resume()
                    } else {
                        rotateDiskAnimation()
                    }
                } else {
                    rotateDiskAnimation()
                }
            }
        }

        skipBackwardButton.setOnClickListener {
            if (mediaPlayer.currentPosition - millisecondsFiveSec >= 0) {
                currentTime.text =
                    millisecondsToTimer(mediaPlayer.currentPosition - millisecondsFiveSec)
            } else {
                currentTime.text = millisecondsToTimer(0)
            }
            mediaPlayer.seekTo(mediaPlayer.currentPosition - millisecondsFiveSec)
            seekBar.progress =
                ((mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()) * 100).toInt()
        }

        skipForwardButton.setOnClickListener {
            if (mediaPlayer.currentPosition + millisecondsFiveSec <= mediaPlayer.duration) {
                currentTime.text =
                    millisecondsToTimer(mediaPlayer.currentPosition + millisecondsFiveSec)
            } else {
                currentTime.text = millisecondsToTimer(mediaPlayer.duration)
            }
            mediaPlayer.seekTo(mediaPlayer.currentPosition + millisecondsFiveSec)
            seekBar.progress =
                ((mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()) * 100).toInt()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    mediaPlayer.seekTo(((mediaPlayer.duration.toFloat() / 100) * p1).toInt())
                    currentTime.text =
                        millisecondsToTimer(((mediaPlayer.duration.toFloat() / 100) * p1).toInt())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(updater)
            playButton.setImageResource(R.drawable.ic_play)
            diskImageAnimator.end()
        }
        // media player operations

        bookTitle.setOnClickListener {
            mediaPlayer.seekTo(0)
            seekBar.progress = 0
            handler.removeCallbacks(updater)
            currentTime.text = millisecondsToTimer(0)
            mediaPlayer.pause()
            chapterTitle.isSelected = false
            playButton.setImageResource(R.drawable.ic_play)
            diskImageAnimator.end()
            val intent = Intent(this@PlayerActivity, BookActivity::class.java)
            intent.putExtra("book", gson.toJson(book))
            startActivity(intent)
        }

        shareButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share this chapter to:")
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "I recommend this chapter to you:\n\nCh.${thisRecord.chapterNumber} ${thisRecord.chapterTitle} of ${bookTitle.text}.\n\n\nInstall Moonice and listen to audiobooks for free.\nhttps://www.youtube.com/watch?v=H0Yirlo6WSU"
            )
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
            prepareMediaPlayer()
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
//        Log.d("Test", "Space")
//        Log.d("Test", "First Has bookmark: $hasBookmark")

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
            if (mediaPlayer.isPlaying) {
                chapterTitle.isSelected = false
                diskImageAnimator.end()
                mediaPlayer.stop()
            }
            finish()
        }
        val bookId = thisRecord.book
        ref.child(NODE_BOOKS).child(NODE_BOOK_DETAILS).child(bookId.toString())
            .addValueEventListener(AppValueEventListener {
                book = it.getValue(Book::class.java)!!
                book.id = it.key?.toLong() ?: 1
                bookTitle.text = book.title
                Picasso.get().load(book.pictureLink)
                    .into(binding.activityPlayerBookImage)
            })
        chapterTitle.text =
            "Ch. ${thisRecord.chapterNumber} ${thisRecord.chapterTitle}"
        likeText.text = thisRecord.like.toString()

        likeRef.addValueEventListener(AppValueEventListener {
            if (it.child(thisRecord.id.toString()).hasChild(user.uid)) {
                likeButton.setImageResource(R.drawable.ic_liked)
                likeText.typeface = Typeface.DEFAULT_BOLD
            } else {
                likeButton.setImageResource(R.drawable.ic_like)
                likeText.typeface = Typeface.DEFAULT
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
                    likeButton.setImageResource(R.drawable.ic_like)
                    likeText.typeface = Typeface.DEFAULT
                    false
                } else {
                    thisRecord.like = thisRecord.like?.plus(1)
                    recordRef.child(thisRecord.id.toString()).child(RECORD_DETAILS_LIKE).setValue(
                        thisRecord.like
                    )
                    likeRef.child(thisRecord.id.toString()).child(user.uid).setValue("Liked")
                    likeButton.setImageResource(R.drawable.ic_liked)
                    likeText.typeface = Typeface.DEFAULT_BOLD
                    false
                }
                likeText.text = thisRecord.like.toString()

            }
        })
    }

    private fun rotateDiskAnimation() {
        diskImageAnimator.repeatCount = Animation.INFINITE
        diskImageAnimator.repeatMode = ObjectAnimator.RESTART
        diskImageAnimator.duration = 5000
        diskImageAnimator.start()

    }

    private val updater = Runnable {
        updateSeekBar()
        currentTime.text = millisecondsToTimer(mediaPlayer.currentPosition)
    }

    private fun updateSeekBar() {
        if (mediaPlayer.isPlaying) {
            seekBar.progress =
                ((mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()) * 100).toInt()
            handler.postDelayed(updater, 500)
        }
    }

    private fun millisecondsToTimer(milliseconds: Int): String {
        var timerString = ""
        val secondsString: String
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
            finish()
        }
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (mediaPlayer.isPlaying) {
            chapterTitle.isSelected = false
            diskImageAnimator.end()
            mediaPlayer.stop()
        }
        finish()
    }
}