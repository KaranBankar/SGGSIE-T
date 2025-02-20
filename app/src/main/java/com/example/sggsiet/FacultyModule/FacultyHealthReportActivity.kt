package com.example.sggsiet.FacultyModule

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sggsiet.Adapter.HealthReportAdapter
import com.example.sggsiet.R
import com.example.sggsiet.dataclasses.HealthReport
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FacultyHealthReportActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HealthReportAdapter
    private val healthReports = mutableListOf<HealthReport>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_faculty_health_report)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HealthReportAdapter(healthReports)
        recyclerView.adapter = adapter

        // Fetch data from Firebase
        fetchHealthReports()
    }

    private fun fetchHealthReports() {
        val database = FirebaseDatabase.getInstance().reference.child("SGGSIE&T").child("healthreports")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                healthReports.clear()
                for (data in snapshot.children) {
                    val report = data.getValue(HealthReport::class.java)
                    if (report != null) {
                        healthReports.add(report)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}