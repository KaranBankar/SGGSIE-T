package com.example.sggsiet.DocterModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sggsiet.R;
import com.google.android.material.card.MaterialCardView;

public class DocterDashboard extends AppCompatActivity {

    private MaterialCardView cardAddReport, cardViewReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docter_dashboard);

        cardAddReport = findViewById(R.id.cardAddReport);
        cardViewReports = findViewById(R.id.cardViewReports);

        // Open Add Student Report Activity
        cardAddReport.setOnClickListener(view -> {
            Intent intent = new Intent(DocterDashboard.this, AddStudentReport.class);
            startActivity(intent);
        });

        // Open View Submitted Reports Activity
        cardViewReports.setOnClickListener(view -> {
            Intent intent = new Intent(DocterDashboard.this, ViewStudentReports.class);
            startActivity(intent);
        });
    }
}
