// CheatRecordAdapter.kt (Updated RecyclerView Adapter with ImageView handling)
package com.example.sggsiet.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sggsiet.R
import com.example.sggsiet.dataclasses.CheatRecord

class CheatRecordAdapter(
    private val cheatRecords: MutableList<CheatRecord>,
    private val onDeleteClick: (CheatRecord) -> Unit
) : RecyclerView.Adapter<CheatRecordAdapter.CheatRecordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheatRecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cheat_record, parent, false)
        return CheatRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheatRecordViewHolder, position: Int) {
        val record = cheatRecords[position]
        holder.bind(record)
        holder.itemView.findViewById<ImageView>(R.id.deleteIcon).setOnClickListener {
            onDeleteClick(record)
        }
    }

    override fun getItemCount(): Int = cheatRecords.size

    class CheatRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val enrollmentNo: TextView = itemView.findViewById(R.id.enrollmentNo)
        private val studentName: TextView = itemView.findViewById(R.id.studentName)
        private val subjectName: TextView = itemView.findViewById(R.id.subjectName)
        private val date: TextView = itemView.findViewById(R.id.date)
        private val imageProof: ImageView = itemView.findViewById(R.id.imageProof)

        fun bind(record: CheatRecord) {
            enrollmentNo.text = record.enrollmentNo
            studentName.text = record.studentName
            subjectName.text = record.subjectName
            date.text = record.date
            Glide.with(itemView.context).load(record.imageUrl).into(imageProof)
        }
    }
}