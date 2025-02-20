package com.example.sggsiet.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sggsiet.R
import com.example.sggsiet.dataclasses.LeaveApplication

class LeaveApplicationAdapter(private val applicationList: List<LeaveApplication>) : RecyclerView.Adapter<LeaveApplicationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewReason: TextView = itemView.findViewById(R.id.textViewReason)
        val textViewStatus: TextView = itemView.findViewById(R.id.textViewStatus)
        val textViewApplicationDate: TextView = itemView.findViewById(R.id.textViewApplicationDate)
        val textViewMobile: TextView = itemView.findViewById(R.id.textViewMobile)
        val textViewSubmissionDate: TextView = itemView.findViewById(R.id.textViewSubmissionDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leave_application, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val application = applicationList[position]
        holder.textViewName.text = application.name
        holder.textViewReason.text = application.reason
        holder.textViewStatus.text = application.status
        holder.textViewApplicationDate.text = application.applicationDate
        holder.textViewMobile.text = application.mobile
        holder.textViewSubmissionDate.text = application.submissionDate
    }

    override fun getItemCount(): Int {
        return applicationList.size
    }
}