package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.sggsiet.Login;
import com.example.sggsiet.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StudentDashboard extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView tvUserName, tvUserDepartment;
    private FirebaseAuth auth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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

        // Initialize Firebase Auth and SharedPreferences
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Initialize UI elements
        tvUserName = findViewById(R.id.tvUserName);
        tvUserDepartment = findViewById(R.id.tvUserDepartment);
        drawerLayout = findViewById(R.id.drawer_layout_one);
        navigationView = findViewById(R.id.navigation_view);

        // Load student details from SharedPreferences


        // Set up menu icon click listener to open drawer
        findViewById(R.id.m_menu).setOnClickListener(v -> drawerLayout.open());

        loadStudentDetails();
        // Handle navigation menu item clicks
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Find card views by ID
        MaterialCardView cardEvents = findViewById(R.id.card_events);
        MaterialCardView cardComplaints = findViewById(R.id.card_complains);
        MaterialCardView cardVoteNow = findViewById(R.id.card_vote);
        MaterialCardView cardHealthReport = findViewById(R.id.card_health);
        MaterialCardView cardCheatRecords = findViewById(R.id.card_cheat);
        MaterialCardView cardBudget = findViewById(R.id.card_budget);

        // Set onClick listeners for navigation
        cardEvents.setOnClickListener(v -> startActivity(new Intent(StudentDashboard.this, EventsActivity.class)));
        cardComplaints.setOnClickListener(v -> startActivity(new Intent(StudentDashboard.this, StudentComplaints.class)));
        cardVoteNow.setOnClickListener(v -> startActivity(new Intent(StudentDashboard.this, VotingActivity.class)));
        cardHealthReport.setOnClickListener(v -> startActivity(new Intent(StudentDashboard.this, HealthReportActivity.class)));
        cardCheatRecords.setOnClickListener(v -> startActivity(new Intent(StudentDashboard.this, CheatRecordsActivity.class)));
        cardBudget.setOnClickListener(v -> startActivity(new Intent(StudentDashboard.this, BudgetRequestTracking.class)));
    }

    // Load student details from SharedPreferences into UI
    // Load student details from SharedPreferences and set them in both UI and Navigation Header
    private void loadStudentDetails() {
        String name = "Welcome,"+sharedPreferences.getString("studentName", "Unknown");
        String department = sharedPreferences.getString("studentDepartment", "No Department");

        // 🔹 Set in Main Dashboard UI
        if (tvUserName != null && tvUserDepartment != null) {
            tvUserName.setText(name);
            tvUserDepartment.setText(department);
        }

        // 🔹 Get Navigation Header View Safely
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView headerUserName = headerView.findViewById(R.id.navUserName);
            TextView headerUserDepartment = headerView.findViewById(R.id.navUserEmail);

            if (headerUserName != null) headerUserName.setText(name);
            if (headerUserDepartment != null) headerUserDepartment.setText(department);
        }
    }



    // Handle navigation drawer item clicks
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            showLogoutDialog();
        }

        drawerLayout.close(); // Close the drawer after item selection
        return true;
    }

    // Show logout confirmation dialog
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", (dialog, which) -> logoutStudent());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Log out the student
    private void logoutStudent() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        boolean isExistingUser = !task.getResult().getSignInMethods().isEmpty();

                        if (isExistingUser) {
                            // 🔹 Sign the user out without deleting the account
                            auth.signOut();
                            clearUserSession();

                            // Navigate to Login Screen
                            Intent intent = new Intent(StudentDashboard.this, Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // 🔹 If user does not exist, remove from Firebase Auth completely
                            user.delete().addOnCompleteListener(deleteTask -> {
                                auth.signOut();
                                clearUserSession();

                                if (deleteTask.isSuccessful()) {
                                    Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Error deleting account", Toast.LENGTH_SHORT).show();
                                }

                                // Navigate to Login Screen
                                Intent intent = new Intent(StudentDashboard.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            });
                        }
                    } else {
                        Toast.makeText(this, "Error checking user existence", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            // If user is null, just sign out and clear session
            auth.signOut();
            clearUserSession();
            startActivity(new Intent(StudentDashboard.this, Login.class));
            finish();
        }
    }

    // Clear user session
    private void clearUserSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // Clears all stored data
        editor.apply();
    }
}
