package com.nulp.moonice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nulp.moonice.databinding.ItemChapterBinding
import com.nulp.moonice.model.AudioRecord
import java.text.DateFormat
import java.text.SimpleDateFormat

interface AudioRecordsActionListener {
    fun onRecordClick(record: AudioRecord)
}

class AudioRecordsAdapter(
    private val actionListener: AudioRecordsActionListener
) : RecyclerView.Adapter<AudioRecordsAdapter.AudioRecordsViewHolder>(), View.OnClickListener {

    var records: List<AudioRecord> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onClick(v: View) {
        val record = v.tag as AudioRecord
        actionListener.onRecordClick(record)
    }

    override fun getItemCount(): Int = records.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioRecordsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChapterBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)

        return AudioRecordsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioRecordsViewHolder, position: Int) {
        val record = records[position]
        with(holder.binding) {
            holder.itemView.tag = record
            itemChapterNumber.text = record.chapterNumber.toString()
            itemChapterTitle.text = record.chapterTitle

            val timeFormat: DateFormat = SimpleDateFormat("HH'h' mm'm' ss's'");
            val strTime = timeFormat.format(record.duration * 1000)

            itemChapterDuration.text = strTime
            itemChapterLike.text = record.like.toString()
        }
    }

    class AudioRecordsViewHolder(
        val binding: ItemChapterBinding
    ) : RecyclerView.ViewHolder(binding.root)
}