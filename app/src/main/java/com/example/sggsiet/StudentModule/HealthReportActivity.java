package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HealthReportActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HealthReportAdapter adapter;
    private List<HealthReportModel> reportList;
    private DatabaseReference databaseReference;
    private String loggedInMobile;

    private ImageView backButton;
    private static final String TAG = "HealthReportActivity"; // Debugging Tag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_report);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_health_reports);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportList = new ArrayList<>();
        adapter = new HealthReportAdapter(this, reportList);
        recyclerView.setAdapter(adapter);

        backButton=findViewById(R.id.m_menu);
        // Handle back button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Retrieve logged-in student's mobile number from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        loggedInMobile = prefs.getString("studentMobile", "").trim(); // Ensure correct key and remove spaces

        if (loggedInMobile.isEmpty()) {
            Toast.makeText(this, "No mobile number found in SharedPreferences!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "SharedPreferences: No mobile number found!");
            return;
        }

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("SGGSIE&T").child("healthreports");

        // Fetch reports for the logged-in student
        fetchHealthReports();
    }

    private void fetchHealthReports() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportList.clear();
                if (!snapshot.exists()) {
                    Log.e(TAG, "Firebase: No health reports found!");
                    Toast.makeText(HealthReportActivity.this, "No reports found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()) {
                    HealthReportModel report = data.getValue(HealthReportModel.class);
                    if (report != null) {
                        String reportMobile = report.getStudentMobile();

                        if (reportMobile != null && reportMobile.trim().equals(loggedInMobile)) {
                            reportList.add(report);
                            Log.d(TAG, "Matched Report Found: " + report.getStudentMobile() + loggedInMobile);
                        }
                    }
                }

                if (reportList.isEmpty()) {
                    Toast.makeText(HealthReportActivity.this, "No reports found for this student!", Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HealthReportActivity.this, "Failed to fetch reports", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Firebase Error: " + error.getMessage());
            }
        });
    }
}
