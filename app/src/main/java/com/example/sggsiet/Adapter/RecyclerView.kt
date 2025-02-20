package com.example.sggsiet.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sggsiet.dataclasses.HealthReport
import com.example.sggsiet.R

class HealthReportAdapter(private val healthReports: List<HealthReport>) :
    RecyclerView.Adapter<HealthReportAdapter.HealthReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_health_report, parent, false)
        return HealthReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: HealthReportViewHolder, position: Int) {
        val report = healthReports[position]
        holder.bind(report)
    }

    override fun getItemCount(): Int = healthReports.size

    class HealthReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.name)
        private val enrollmentNo: TextView = itemView.findViewById(R.id.enrollmentNo)
        private val healthIssue: TextView = itemView.findViewById(R.id.healthIssue)
        private val treatment: TextView = itemView.findViewById(R.id.treatment)
        private val year: TextView = itemView.findViewById(R.id.year)

        fun bind(report: HealthReport) {
            name.text = report.name
            enrollmentNo.text = report.enrollmentno
            healthIssue.text = report.healthissue
            treatment.text = report.treatment
            year.text = report.year
        }
    }
}