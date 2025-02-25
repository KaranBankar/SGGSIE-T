package com.example.sggsiet.FacultyModule

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sggsiet.Adapter.LeaveRequestAdapter
import com.example.sggsiet.R
import com.example.sggsiet.dataclasses.LeaveRequest
import com.google.firebase.database.*

class FacultyLeaveApprovalActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var leaveRequestAdapter: LeaveRequestAdapter
    private lateinit var databaseLeaveRequests: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_leave_approval)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        leaveRequestAdapter = LeaveRequestAdapter(mutableListOf(), ::approveLeaveRequest, ::rejectLeaveRequest)
        recyclerView.adapter = leaveRequestAdapter

        databaseLeaveRequests = FirebaseDatabase.getInstance().getReference("LeaveRequests")
        fetchLeaveRequests()
    }

    private fun fetchLeaveRequests() {
        databaseLeaveRequests.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<LeaveRequest>()
                for (requestSnapshot in snapshot.children) {
                    val mapData = requestSnapshot.value as? Map<String, String>
                    if (mapData != null) {
                        val leaveRequest = LeaveRequest(
                            studentName = mapData["studentName"] ?: "",
                            studentEmail = mapData["studentEmail"] ?: "",
                            studentMobile = mapData["studentMobile"] ?: "",
                            studentDept = mapData["studentDept"] ?: "",
                            duration = mapData["duration"] ?: "",
                            reason = mapData["reason"] ?: "",
                            status = mapData["status"] ?: "pending"
                        )
                        tempList.add(leaveRequest)
                    } else {
                        Toast.makeText(this@FacultyLeaveApprovalActivity, "Invalid data format.", Toast.LENGTH_SHORT).show()
                    }
                }
                leaveRequestAdapter.updateList(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FacultyLeaveApprovalActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun approveLeaveRequest(leaveRequest: LeaveRequest) {
        updateLeaveRequestStatus(leaveRequest, "Approved")
    }

    private fun rejectLeaveRequest(leaveRequest: LeaveRequest) {
        updateLeaveRequestStatus(leaveRequest, "Rejected")
    }

    private fun updateLeaveRequestStatus(leaveRequest: LeaveRequest, newStatus: String) {
        val query = databaseLeaveRequests.orderByChild("studentEmail").equalTo(leaveRequest.studentEmail)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (requestSnapshot in snapshot.children) {
                        requestSnapshot.ref.child("status").setValue(newStatus)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@FacultyLeaveApprovalActivity,
                                    "Status updated to $newStatus",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@FacultyLeaveApprovalActivity,
                                    "Failed to update status",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    Toast.makeText(this@FacultyLeaveApprovalActivity, "Request not found!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@FacultyLeaveApprovalActivity,
                    "Database error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}
