package com.example.sggsiet.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sggsiet.R
import com.example.sggsiet.dataclasses.Complaint
import com.google.android.material.imageview.ShapeableImageView

class ComplaintAdapter(private val complaints: List<Complaint>) : RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>() {

    class ComplaintViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val complaintType: TextView = itemView.findViewById(R.id.tvComplaintType)
        val description: TextView = itemView.findViewById(R.id.tvDescription)
        val image: ShapeableImageView = itemView.findViewById(R.id.ivComplaintImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_complaint, parent, false)
        return ComplaintViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        val complaint = complaints[position]
        holder.title.text = complaint.title
        holder.complaintType.text = complaint.complaintType
        holder.description.text = complaint.description
        Glide.with(holder.itemView.context)
            .load(complaint.imageUrl)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return complaints.size
    }
}