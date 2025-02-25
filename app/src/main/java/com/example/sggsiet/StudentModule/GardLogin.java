package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sggsiet.GardModule.GardDashBoardActivity;
import com.example.sggsiet.R;

public class GardLogin extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Spinner gardTypeSpinner;
    private Button loginButton;
    private SharedPreferences sharedPreferences;

    private static final String DEFAULT_EMAIL = "sggsgard@gmail.com";
    private static final String DEFAULT_PASSWORD = "gard@123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gard_login);

        // Initialize Views
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        gardTypeSpinner = findViewById(R.id.gard_type_spinner);
        loginButton = findViewById(R.id.login_button);

        // Set up spinner with gard types
        String[] gardTypes = {"gymgard", "auditoriamgard", "tenniscourtgard"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, gardTypes);
        gardTypeSpinner.setAdapter(adapter);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("GardLoginPrefs", Context.MODE_PRIVATE);

        // ✅ Check if user is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToDashboard();
        }

        // Login button click listener
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String selectedGardType = gardTypeSpinner.getSelectedItem().toString();

            if (email.equals(DEFAULT_EMAIL) && password.equals(DEFAULT_PASSWORD)) {
                // ✅ Store login details in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("gardEmail", email);
                editor.putString("gardType", selectedGardType);
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                // Navigate to dashboard
                navigateToDashboard();
            } else {
                Toast.makeText(GardLogin.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ Navigate to dashboard and finish login activity
    private void navigateToDashboard() {
        Intent intent = new Intent(GardLogin.this, GardDashBoardActivity.class);
        startActivity(intent);
        finish();
    }
}
