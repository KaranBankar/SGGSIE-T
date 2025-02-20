package com.example.sggsiet.FacultyModule

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView // Correct import
import com.example.sggsiet.Adapter.CheatRecordAdapter
import com.example.sggsiet.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.example.sggsiet.databinding.ActivityFacultyCheatRecordsBinding
import com.example.sggsiet.dataclasses.CheatRecord
import com.google.android.material.card.MaterialCardView

class FacultyCheatRecordsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFacultyCheatRecordsBinding
    private lateinit var adapter: CheatRecordAdapter
    private val cheatRecords = mutableListOf<CheatRecord>()
    private val database = FirebaseDatabase.getInstance().reference.child("CheatRecords")
    private val storage = FirebaseStorage.getInstance().reference

    private var imageUri: Uri? = null
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacultyCheatRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CheatRecordAdapter(cheatRecords) { record ->
            deleteRecord(record)
        }
        binding.recyclerView.adapter = adapter

        // Set click listener for the add record button
        binding.addRecordButton.setOnClickListener {
            showAddRecordDialog()
        }

        loadRecords()
    }

    private fun showAddRecordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_record, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        dialogView.findViewById<MaterialCardView>(R.id.uploadImageButton).setOnClickListener {
            getContent.launch("image/*")
        }

        dialogView.findViewById<MaterialCardView>(R.id.saveButton).setOnClickListener {
            val enrollmentNo = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.enrollmentNo).text.toString()
            val studentName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.studentName).text.toString()
            val subjectName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.subjectName).text.toString()
            val date = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.date).text.toString()


            if (enrollmentNo.isNotEmpty() && studentName.isNotEmpty() && subjectName.isNotEmpty() && date.isNotEmpty() && imageUri != null) {
                uploadImage(enrollmentNo, studentName, subjectName, date)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun uploadImage(enrollmentNo: String, studentName: String, subjectName: String, date: String) {
        val storageRef = storage.child("images/${System.currentTimeMillis()}.jpg")
        imageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    // Get the download URL after successful upload
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveRecord(enrollmentNo, studentName, subjectName, date, downloadUri.toString())
                    }.addOnFailureListener { exception ->
                        Toast.makeText(this, "Failed to get download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveRecord(enrollmentNo: String, studentName: String, subjectName: String, date: String, imageUrl: String) {
        val record = CheatRecord(enrollmentNo, studentName, subjectName, date, imageUrl)
        database.child(enrollmentNo).setValue(record).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Record saved successfully", Toast.LENGTH_SHORT).show()
                loadRecords()
            } else {
                Toast.makeText(this, "Failed to save record", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadRecords() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cheatRecords.clear()
                for (data in snapshot.children) {
                    val record = data.getValue(CheatRecord::class.java)
                    if (record != null) {
                        cheatRecords.add(record)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FacultyCheatRecordsActivity, "Failed to load records", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteRecord(record: CheatRecord) {
        database.child(record.enrollmentNo).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Record deleted successfully", Toast.LENGTH_SHORT).show()
                loadRecords()
            } else {
                Toast.makeText(this, "Failed to delete record", Toast.LENGTH_SHORT).show()
            }
        }
    }
}