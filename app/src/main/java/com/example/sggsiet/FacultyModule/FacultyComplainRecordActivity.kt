package com.example.sggsiet.FacultyModule

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sggsiet.Adapter.ComplaintAdapter
import com.example.sggsiet.R
import com.example.sggsiet.dataclasses.Complaint
import com.google.firebase.database.*

class FacultyComplainRecordActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var complaintAdapter: ComplaintAdapter
    private lateinit var complaintList: MutableList<Complaint>
    private lateinit var databaseComplaints: DatabaseReference
    private lateinit var databaseResolvedComplaints: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_faculty_complain_record)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        complaintList = mutableListOf()
        complaintAdapter = ComplaintAdapter(complaintList)
        recyclerView.adapter = complaintAdapter

        // Initialize database references
        databaseComplaints = FirebaseDatabase.getInstance().getReference("complaints")
        //databaseResolvedComplaints = FirebaseDatabase.getInstance().getReference("resolved_complaints")

        // Fetch complaints and resolved complaints
        fetchComplaints()
       // fetchResolvedComplaints()
    }

    private fun fetchComplaints() {
        databaseComplaints.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintList.clear()
                for (complaintSnapshot in snapshot.children) {
                    val complaint = complaintSnapshot.getValue(Complaint::class.java)
                    complaint?.let { complaintList.add(it) }
                }
                complaintAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    private fun fetchResolvedComplaints() {
        databaseResolvedComplaints.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (resolvedSnapshot in snapshot.children) {
                    val resolvedComplaint = resolvedSnapshot.getValue(Complaint::class.java)
                    resolvedComplaint?.let { complaintList.add(it) }
                }
                Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show();

                complaintAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }
}