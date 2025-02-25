package com.example.sggsiet.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sggsiet.R
import com.example.sggsiet.dataclasses.LeaveRequest

class LeaveRequestAdapter(
    private var leaveRequestList: MutableList<LeaveRequest>,
    private val onApproveClick: (LeaveRequest) -> Unit,
    private val onRejectClick: (LeaveRequest) -> Unit
) : RecyclerView.Adapter<LeaveRequestAdapter.LeaveRequestViewHolder>() {

    inner class LeaveRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvReason: TextView = itemView.findViewById(R.id.tvReason)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnApprove: Button = itemView.findViewById(R.id.btnApprove)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leave_request, parent, false)
        return LeaveRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaveRequestViewHolder, position: Int) {
        val leaveRequest = leaveRequestList[position]
        holder.tvStudentName.text = leaveRequest.studentName
        holder.tvReason.text = leaveRequest.reason
        holder.tvStatus.text = leaveRequest.status

        holder.btnApprove.setOnClickListener { onApproveClick(leaveRequest) }
        holder.btnReject.setOnClickListener { onRejectClick(leaveRequest) }
    }

    override fun getItemCount(): Int = leaveRequestList.size

    fun updateList(newList: List<LeaveRequest>) {
        leaveRequestList.clear()
        leaveRequestList.addAll(newList)
        notifyDataSetChanged()
    }
}
