package com.example.sggsiet

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sggsiet.FacultyModule.AproveEvents
import com.example.sggsiet.FacultyModule.FacultyCheatRecordsActivity
import com.example.sggsiet.FacultyModule.FacultyHealthReportActivity
import com.example.sggsiet.FacultyModule.FacultyLeaveApplicationActivity
import com.example.sggsiet.FacultyModule.SolveDoubt
import com.example.sggsiet.databinding.ActivityFacultyModuleBinding
import com.example.sggsiet.dataclasses.CheatRecord
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class FacultyModuleActivity : AppCompatActivity() {
    lateinit var binding: ActivityFacultyModuleBinding

    // Declare the ActivityResultLauncher as a class-level variable
    private lateinit var getContent: ActivityResultLauncher<String>
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFacultyModuleBinding.inflate(layoutInflater)


        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize the ActivityResultLauncher
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = uri
                Toast.makeText(this, "Image selected: $uri", Toast.LENGTH_SHORT).show()
            }
        }

        // Check and request permissions only if not granted
        checkAndRequestPermission()

        // Set up UI components
        applyWindowInsets()
        setupToolbar()
        setupNavigationDrawer()

        binding.layoutEventAprove.setOnClickListener {
            val i = Intent(this, AproveEvents::class.java)
            startActivity(i)

        }
        // Handle button clicks
        binding.CheatRecords.setOnClickListener {
            val i = Intent(this, FacultyCheatRecordsActivity::class.java)
            startActivity(i)
        }

        binding.healthReport.setOnClickListener{
            val i = Intent(this, SolveDoubt::class.java)
            startActivity(i)
        }

        binding.leaveApplication.setOnClickListener{
            val i = Intent(this, FacultyLeaveApplicationActivity::class.java)
            startActivity(i)
        }

        binding.ElectionConduct.setOnClickListener{
            val i = Intent(this, DisplayElectionPositions::class.java)
            startActivity(i)
        }
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, do nothing
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE_READ_EXTERNAL_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the gallery
                openGallery()
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied. Cannot access gallery.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        // Launch the image picker
        getContent.launch("image/*")
    }

    private fun uploadImage(enrollmentNo: String, studentName: String, subjectName: String, date: String) {
        if (imageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a reference to the image file in Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL after successful upload
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Save the record with the download URL
                    saveRecord(enrollmentNo, studentName, subjectName, date, downloadUri.toString())
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to get download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveRecord(enrollmentNo: String, studentName: String, subjectName: String, date: String, imageUrl: String) {
        val record = CheatRecord(enrollmentNo, studentName, subjectName, date, imageUrl)
        val databaseRef = FirebaseDatabase.getInstance().reference.child("CheatRecords").child(enrollmentNo)

        databaseRef.setValue(record)
            .addOnSuccessListener {
                Toast.makeText(this, "Record saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to save record: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply system bar insets as padding
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets // Required to propagate insets further
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupNavigationDrawer() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // Handle navigation drawer item clicks here
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    companion object {
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 100
    }
}