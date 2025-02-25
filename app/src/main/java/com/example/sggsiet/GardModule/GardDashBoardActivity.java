package com.example.sggsiet.GardModule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sggsiet.R;
import com.example.sggsiet.StudentModule.GardLogin;
import com.google.android.material.card.MaterialCardView;

public class GardDashBoardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private MaterialCardView cardApprove, cardConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gard_dash_board);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // ✅ Ensures menu items appear

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("GardLoginPrefs", Context.MODE_PRIVATE);

        // ✅ Initialize CardViews
        cardApprove = findViewById(R.id.cardApprove);
        cardConfirm = findViewById(R.id.cardConfirmed);

        // ✅ Set click listeners for cards
        cardApprove.setOnClickListener(v -> {
            Intent intent = new Intent(GardDashBoardActivity.this, GardApproveActivity.class);
            startActivity(intent);
        });

        cardConfirm.setOnClickListener(v -> {
            Intent intent = new Intent(GardDashBoardActivity.this, GardConfirmedActivity.class);
            startActivity(intent);
        });
    }


    // Inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu_gard, menu);
        return true;
    }

    // Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ✅ Logout function
    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // Clear all stored data
        editor.apply();

        // Redirect to login
        Intent intent = new Intent(GardDashBoardActivity.this, GardLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }
}
