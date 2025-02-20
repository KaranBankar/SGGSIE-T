package com.example.sggsiet.StudentModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sggsiet.R;
import com.google.android.material.card.MaterialCardView;

public class StudentDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find card views by ID
        MaterialCardView cardEvents = findViewById(R.id.card_events);
        MaterialCardView cardComplaints = findViewById(R.id.card_complains);
        MaterialCardView cardVoteNow = findViewById(R.id.card_vote);
        MaterialCardView cardHealthReport = findViewById(R.id.card_health);
        MaterialCardView cardCheatRecords = findViewById(R.id.card_cheat);
        MaterialCardView cardBudget = findViewById(R.id.card_budget);

        // Set onClick listeners for navigation
        cardEvents.setOnClickListener(v -> startActivity(new Intent(StudentDashboard.this, EventsActivity.class)));
        cardComplaints.setOnClickListener(v -> startActivity(new Intent(StudentDashboard.this, ComplaintActivity.class)));
        cardVoteNow.setOnClickListener(v -> startActivity(new Intent(StudentDashboard.this, VotingActivity.class)));
        cardHealthReport.setOnClickListener(v -> startActivity(new Intent(StudentDashboard.this, HealthReportActivity.class)));
        cardCheatRecords.setOnClickListener(v -> startActivity(new Intent(StudentDashboard.this, CheatRecordsActivity.class)));
        cardBudget.setOnClickListener(v -> startActivity(new Intent(StudentDashboard.this, BudgetActivity.class)));
    }
}
