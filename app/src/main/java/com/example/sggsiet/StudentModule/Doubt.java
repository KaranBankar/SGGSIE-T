package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Doubt extends AppCompatActivity {

    private RecyclerView facultyRecyclerView;
    private FacultyAdapter facultyAdapter;
    private List<FacultyModel> facultyList = new ArrayList<>();
    private String studentDepartment;  // Student's department from SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doubt);

        // Retrieve student's department from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        studentDepartment = sharedPreferences.getString("studentDept", "");

        facultyRecyclerView = findViewById(R.id.facultyRecyclerView);
        facultyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        facultyAdapter = new FacultyAdapter(facultyList, this);
        facultyRecyclerView.setAdapter(facultyAdapter);

        // Fetch faculty data from Firebase Storage
        fetchFacultyData();
    }

    private void fetchFacultyData() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("dummy_faculty_data.csv");

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.ByteArrayInputStream(bytes)))) {
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {  // Skip header line
                        isFirstLine = false;
                        continue;
                    }

                    String[] columns = line.split(",");
                    if (columns.length < 5) continue;

                    String email = columns[0].trim();
                    String name = columns[1].trim();
                    String mobile = columns[2].trim();
                    String department = columns[3].trim();
                    String position = columns[4].trim();

                    // Filter faculty based on student's department
                    if (department.equalsIgnoreCase(studentDepartment)) {
                        facultyList.add(new FacultyModel(name, email, mobile, department, position));
                    }
                }
                facultyAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                Toast.makeText(this, "Error reading data", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show());
    }
}
