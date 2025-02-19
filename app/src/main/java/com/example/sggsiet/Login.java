package com.example.sggsiet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sggsiet.AdminModule.PrincipalDashboard;
import com.example.sggsiet.DocterModule.DocterDashboard;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Login extends AppCompatActivity {

    private AutoCompleteTextView spinnerLoginType;
    private EditText etEmail, etOtp;
    private MaterialButton btnLogin;
    private MaterialTextView btnGetOtp;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private String generatedOtp;

    private static final int SMS_PERMISSION_CODE = 101;
    private static final String DEFAULT_PASSWORD = "Default@123";  // Default password for Firebase Auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        spinnerLoginType = findViewById(R.id.spinnerLoginType);
        etEmail = findViewById(R.id.etEmail);
        etOtp = findViewById(R.id.etOtp);
        btnLogin = findViewById(R.id.btnLogin);
        btnGetOtp = findViewById(R.id.tvGetOtp);

        String[] userTypes = {"Administration", "Faculty", "Doctor", "Student"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, userTypes);
        spinnerLoginType.setAdapter(adapter);

        btnGetOtp.setOnClickListener(v -> sendOtp());
        btnLogin.setOnClickListener(v -> verifyOtp());
    }

    private void sendOtp() {
        String email = etEmail.getText().toString().trim();
        String userType = spinnerLoginType.getText().toString();
        String phoneNumber = "";

        switch (userType) {
            case "Administration":
                if (email.equals("admin@gmail.com")) phoneNumber = "8261830043";
                break;
            case "Faculty":
                if (email.equals("faculty@gmail.com")) phoneNumber = "9307879687";
                break;
            case "Doctor":
                if (email.equals("doctor@gmail.com")) phoneNumber = "9322067937";
                break;
            case "Student":
                Toast.makeText(this, "Student login is not implemented yet", Toast.LENGTH_SHORT).show();
                return;
        }

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Invalid email for selected role", Toast.LENGTH_SHORT).show();
            return;
        }

        generatedOtp = String.valueOf(new Random().nextInt(899999) + 100000);
        requestSmsPermission(phoneNumber, generatedOtp);
    }

    private void requestSmsPermission(String phoneNumber, String otp) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sendSms(phoneNumber, otp);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }

    private void sendSms(String phoneNumber, String otp) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "Your OTP is: " + otp, null, null);
            Toast.makeText(this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyOtp() {
        String enteredOtp = etOtp.getText().toString().trim();
        if (generatedOtp == null || !generatedOtp.equals(enteredOtp)) {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = etEmail.getText().toString().trim();
        String userType = spinnerLoginType.getText().toString();

        auth.createUserWithEmailAndPassword(email, DEFAULT_PASSWORD)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            saveUserToDatabase(firebaseUser.getUid(), email, userType);
                        }
                    } else {
                        Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(String userId, String email, String userType) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("userType", userType);

        databaseReference.child(userId).setValue(userMap)
                .addOnSuccessListener(unused -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("userType", userType);
                    editor.apply();

                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    navigateToDashboard(userType);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void navigateToDashboard(String userType) {
        Intent intent;
        switch (userType) {
            case "Administration":
                intent = new Intent(this, PrincipalDashboard.class);
                break;
            case "Faculty":
                intent = new Intent(this, FacultyModuleActivity.class);
                break;
            case "Doctor":
                intent = new Intent(this, DocterDashboard.class);
                break;
            default:
                Toast.makeText(this, "Invalid User Type", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendOtp();
            } else {
                Toast.makeText(this, "SMS permission required to send OTP", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
