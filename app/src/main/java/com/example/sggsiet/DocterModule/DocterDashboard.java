package com.example.sggsiet.DocterModule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.sggsiet.Login;
import com.example.sggsiet.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DocterDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private MaterialCardView cardAddReport, cardViewReports,card_appointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docter_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hello Doctor");

        // Setup Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Find Cards
        cardAddReport = findViewById(R.id.cardAddReport);
        cardViewReports = findViewById(R.id.cardViewReports);
        card_appointments=findViewById(R.id.cardViewAppointments);


        // Click Listeners
        cardAddReport.setOnClickListener(view -> startActivity(new Intent(DocterDashboard.this, AddStudentReport.class)));
        cardViewReports.setOnClickListener(view -> startActivity(new Intent(DocterDashboard.this, ViewStudentReports.class)));
        card_appointments.setOnClickListener(view -> startActivity(new Intent(DocterDashboard.this, ViewMyAppointments.class)));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this, "Home Selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            logoutUser();
        } else if (id == R.id.nav_feedback) {
            sendFeedback();
        } else if (id == R.id.nav_share) {
            shareApp();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void logoutUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
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
                            Intent intent = new Intent(DocterDashboard.this, Login.class);
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
                                Intent intent = new Intent(DocterDashboard.this, Login.class);
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
            startActivity(new Intent(DocterDashboard.this, Login.class));
            finish();
        }
    }

    // ✅ Clears User Data from SharedPreferences (Prevents Auto-Login)
    private void clearUserSession() {
        SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }



    private void sendFeedback() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:feedback@sggsiet.edu"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Doctor Module Feedback");
        startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out the SGGSIE&T Health App! Download now: [App Link]");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}