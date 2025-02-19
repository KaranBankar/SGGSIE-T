package com.example.sggsiet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.annotation.ElementType;
import java.util.Random;

public class Login extends AppCompatActivity {

    private AutoCompleteTextView spinnerLoginType;
    private EditText etEmail, etOtp;
    private Button btnLogin;
    private String generatedOtp;

    private static final int SMS_PERMISSION_CODE = 101;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        spinnerLoginType = findViewById(R.id.spinnerLoginType);
        etEmail = findViewById(R.id.etEmail);
        etOtp = findViewById(R.id.etOtp);
        btnLogin = findViewById(R.id.btnLogin);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Dropdown options
        String[] userTypes = {"Administration", "Faculty", "Doctor", "Student"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, userTypes);
        spinnerLoginType.setAdapter(adapter);

        findViewById(R.id.tvGetOtp).setOnClickListener(v -> sendOtp());
        btnLogin.setOnClickListener(v -> verifyLogin());
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

    private void verifyLogin() {
        String enteredOtp = etOtp.getText().toString().trim();
        String userType = spinnerLoginType.getText().toString();

        if (generatedOtp == null || !generatedOtp.equals(enteredOtp)) {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userType", userType);
        editor.apply();

        Intent intent;
        switch (userType) {
            case "Administration":
                intent = new Intent(this, DisplayElectionPositions.class);
                break;
            case "Faculty":
                intent = new Intent(this, DisplayElectionPositions.class);
                break;
            case "Doctor":
                intent = new Intent(this, DisplayElectionPositions.class);
                break;
            default:
                Toast.makeText(this, "Invalid login type", Toast.LENGTH_SHORT).show();
                return;
        }

        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
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
