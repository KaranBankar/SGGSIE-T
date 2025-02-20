package com.example.sggsiet.FacultyModule

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sggsiet.Adapter.LeaveApplicationAdapter
import com.example.sggsiet.R
import com.example.sggsiet.dataclasses.LeaveApplication
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class FacultyLeaveApplicationActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextReason: EditText
    private lateinit var editTextMobile: EditText
    private lateinit var textViewDate: TextView
    private lateinit var buttonSelectDate: Button
    private lateinit var buttonSubmit: Button
    private lateinit var buttonCheckApplications: Button
    private lateinit var editTextCheckEmail: EditText
    private lateinit var recyclerViewApplications: RecyclerView
    private lateinit var applicationList: MutableList<LeaveApplication>
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_leave_application)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("leaveApplications")
        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextReason = findViewById(R.id.editTextReason)
        editTextMobile = findViewById(R.id.editTextMobile)
        textViewDate = findViewById(R.id.textViewDate)
        buttonSelectDate = findViewById(R.id.buttonSelectDate)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        editTextCheckEmail = findViewById(R.id.editTextCheckEmail) // New EditText for checking applications
        buttonCheckApplications = findViewById(R.id.buttonCheckApplications) // New Button for checking applications
        recyclerViewApplications = findViewById(R.id.recyclerViewApplications)
        applicationList = mutableListOf()

        // Set up RecyclerView
        recyclerViewApplications.layoutManager = LinearLayoutManager(this)
        recyclerViewApplications.adapter = LeaveApplicationAdapter(applicationList)

        // Set up button listeners
        buttonSelectDate.setOnClickListener {
            showDatePicker()
        }

        buttonSubmit.setOnClickListener {
            submitApplication()
        }

        buttonCheckApplications.setOnClickListener {
            fetchApplications() // Fetch applications based on the entered email
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = Calendar.getInstance()
            date.set(selectedYear, selectedMonth, selectedDay)
            selectedDate = sdf.format(date.time)
            textViewDate.text = selectedDate
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun submitApplication() {
        val name = editTextName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val reason = editTextReason.text.toString().trim()
        val mobile = editTextMobile.text.toString().trim()
        val submissionDate = System.currentTimeMillis().toString() // Use current timestamp as submission date

        if (name.isEmpty() || email.isEmpty() || reason.isEmpty() || mobile.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val applicationId = database.push().key ?: return
        val leaveApplication = LeaveApplication(name, email, reason, "Pending", selectedDate, mobile, submissionDate)

        database .child(applicationId).setValue(leaveApplication).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Application submitted", Toast.LENGTH_SHORT).show()
                clearFields()
                fetchApplications() // Refresh the list of applications
            } else {
                Toast.makeText(this, "Failed to submit application: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearFields() {
        editTextName.text.clear()
        editTextEmail.text.clear()
        editTextReason.text.clear()
        editTextMobile.text.clear()
        textViewDate.text = "Select Date"
        selectedDate = ""
    }

    private fun fetchApplications() {
        val email = editTextCheckEmail.text.toString().trim() // Get the email from the new EditText
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email to view applications", Toast.LENGTH_SHORT).show()
            return
        }

        // Query to fetch applications where the email matches the entered email
        val query: Query = database.orderByChild("email").equalTo(email)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                applicationList.clear() // Clear the existing list
                for (applicationSnapshot in snapshot.children) {
                    val application = applicationSnapshot.getValue(LeaveApplication::class.java)
                    application?.let { applicationList.add(it) } // Add the application to the list
                }
                // Notify the adapter of the data change
                recyclerViewApplications.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FacultyLeaveApplicationActivity, "Failed to load applications: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}