package com.nulp.moonice.adapter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nulp.moonice.R
import com.nulp.moonice.databinding.ItemChapterBinding
import com.nulp.moonice.model.AudioRecord
import com.nulp.moonice.utils.AppValueEventListener
import com.nulp.moonice.utils.FIREBASE_URL
import com.nulp.moonice.utils.NODE_BOOKS
import com.nulp.moonice.utils.NODE_RECORDS_LIKES
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

interface AudioRecordsActionListener {
    fun onRecordClick(record: AudioRecord)
}

class AudioRecordsAdapter(
    private val actionListener: AudioRecordsActionListener,
    private val audioRecordList: List<AudioRecord>
) : RecyclerView.Adapter<AudioRecordsAdapter.AudioRecordsViewHolder>(), View.OnClickListener {

    override fun onClick(v: View) {
        val record = v.tag as AudioRecord
        actionListener.onRecordClick(record)
    }

    override fun getItemCount(): Int = audioRecordList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioRecordsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChapterBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)

        return AudioRecordsViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: AudioRecordsViewHolder, position: Int) {
        val record = audioRecordList[position]
        with(holder.binding) {
            holder.itemView.tag = record
            itemChapterNumber.text = record.chapterNumber.toString()
            itemChapterTitle.text = record.chapterTitle

            val timeFormat: DateFormat = SimpleDateFormat("HH'h' mm'm' ss's'")
            timeFormat.timeZone = TimeZone.getTimeZone("UTC")
            val strTime = timeFormat.format(record.duration)

            itemChapterDuration.text = strTime
            itemChapterLike.text = record.like.toString()
            val ref = FirebaseDatabase.getInstance(FIREBASE_URL).reference
            val likeRef = ref.child(NODE_BOOKS).child(NODE_RECORDS_LIKES)
            val user = FirebaseAuth.getInstance().currentUser
            likeRef.addValueEventListener(AppValueEventListener {
                if (user != null) {
                    if (it.child(record.id.toString()).hasChild(user.uid)) {
                        like.setImageResource(R.drawable.ic_liked)
                        itemChapterLike.typeface = Typeface.DEFAULT_BOLD

                    } else {
                        like.setImageResource(R.drawable.ic_like)
                        itemChapterLike.typeface = Typeface.DEFAULT
                    }
                }
            })
        }
    }

    class AudioRecordsViewHolder(
        val binding: ItemChapterBinding
    ) : RecyclerView.ViewHolder(binding.root)
}