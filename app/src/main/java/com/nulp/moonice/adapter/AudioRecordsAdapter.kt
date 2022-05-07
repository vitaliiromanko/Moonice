package com.nulp.moonice.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.nulp.moonice.databinding.ItemChapterBinding
import com.nulp.moonice.model.AudioRecord
import com.nulp.moonice.utils.FIREBASE_URL
import java.sql.Time
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

interface AudioRecordsActionListener {
    fun onRecordClick(record: AudioRecord)
}

class AudioRecordsAdapter(
    private val actionListener: AudioRecordsActionListener,
    private val audioRecordList: ArrayList<AudioRecord>
) : RecyclerView.Adapter<AudioRecordsAdapter.AudioRecordsViewHolder>(), View.OnClickListener {

//    var records: List<AudioRecord> = emptyList()
//        set(newValue) {
//            field = newValue
//            notifyDataSetChanged()
//        }

    override fun onClick(v: View) {
        val record = v.tag as AudioRecord
        actionListener.onRecordClick(record)
    }

    //    override fun getItemCount(): Int = records.size
    override fun getItemCount(): Int = audioRecordList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioRecordsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChapterBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)

        return AudioRecordsViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: AudioRecordsViewHolder, position: Int) {
//        val record = records[position]
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
        }
    }

    class AudioRecordsViewHolder(
        val binding: ItemChapterBinding
    ) : RecyclerView.ViewHolder(binding.root)
}