package com.example.sggsiet;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sggsiet.AdminModule.PrincipalDashboard;
import com.example.sggsiet.DocterModule.DocterDashboard;
import com.example.sggsiet.StudentModule.StudentDashboard;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
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
    private static final String DEFAULT_PASSWORD = "Default@123";

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

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userType.equals("Student")) {
            fetchStudentPhoneNumber(email);
        } else {
            String phoneNumber = getStaticPhoneNumber(userType, email);
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Invalid email for selected role", Toast.LENGTH_SHORT).show();
                return;
            }
            generateAndSendOtp(phoneNumber);
        }
    }

    private void fetchStudentPhoneNumber(String email) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("dummy_student_data.csv");

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            Log.d("CSV_FETCH", "CSV file fetched successfully. Size: " + bytes.length + " bytes");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.ByteArrayInputStream(bytes)))) {
                String line;
                boolean found = false;

                while ((line = reader.readLine()) != null) {
                    Log.d("CSV_LINE", "Processing line: " + line);

                    String[] columns = line.split(",");

                    if (columns.length < 6) continue; // Ensure at least 6 columns exist

                    String csvEmail = columns[2].trim();
                    String phoneNumber = columns[5].trim();

                    String enrollmentNo = columns[0].trim();
                    String studentName = columns[1].trim();
                    String department = columns[3].trim();
                    String year = columns[4].trim();
                    String mobile = columns[5].trim();
                    String emergencyContact = columns[6].trim();
                    String position = columns[7].trim();



                    Log.d("EMAIL_CHECK", "Comparing: " + csvEmail + " vs " + email.trim());

                    if (csvEmail.equalsIgnoreCase(email.trim())) {
                        if (!phoneNumber.isEmpty()) {
                            found = true;


                            saveStudentDetailsToPreferences(enrollmentNo, studentName, csvEmail, department, year, mobile, emergencyContact, position);
                            generateAndSendOtp(phoneNumber);
                            Log.d("CSV_SUCCESS", "Phone number found: " + phoneNumber);
                        } else {
                            Log.e("CSV_ERROR", "Phone number missing for email: " + email);
                            Toast.makeText(this, "Phone number not found for this student", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }

                if (!found) {
                    Log.e("CSV_ERROR", "Email not found in records: " + email);
                    Toast.makeText(this, "Email not found in records", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("CSV_ERROR", "Error reading student data", e);
                Toast.makeText(this, "Error reading student data", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("CSV_ERROR", "Failed to fetch student data", e);
            Toast.makeText(this, "Failed to fetch student data", Toast.LENGTH_SHORT).show();
        });
    }



    private void generateAndSendOtp(String phoneNumber) {
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

        // 🔹 Sign out the user before re-authenticating
        if (auth.getCurrentUser() != null) {
            auth.signOut();
        }

        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean isExistingUser = !task.getResult().getSignInMethods().isEmpty();

                if (isExistingUser) {
                    // 🔹 Sign in the user again after signing out
                    auth.signInWithEmailAndPassword(email, DEFAULT_PASSWORD)
                            .addOnCompleteListener(signInTask -> {
                                if (signInTask.isSuccessful()) {
                                    saveUserSession(userType);
                                    navigateToDashboard(userType);
                                } else {
                                    Toast.makeText(this, "Login failed: " + signInTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // 🔹 Create a new user if they don’t exist
                    auth.createUserWithEmailAndPassword(email, DEFAULT_PASSWORD)
                            .addOnCompleteListener(registerTask -> {
                                if (registerTask.isSuccessful()) {
                                    saveUserSession(userType);
                                    navigateToDashboard(userType);
                                } else {
                                    Toast.makeText(this, "Registration failed: " + registerTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(this, "Error checking user existence", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveUserSession(String userType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userType", userType);
        editor.apply();
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
            case "Student":
                intent = new Intent(this, StudentDashboard.class);
                break;
            default:
                Toast.makeText(this, "Invalid user type", Toast.LENGTH_SHORT).show();
                return;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void saveUserData(String email, String userType) {
        String uid = auth.getCurrentUser().getUid();
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("userType", userType);

        databaseReference.child(uid).setValue(userMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "User data saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getStaticPhoneNumber(String userType, String email) {
        HashMap<String, String> phoneNumbers = new HashMap<>();
        phoneNumbers.put("Administration_admin@gmail.com", "8261830043");
        phoneNumbers.put("Faculty_faculty@gmail.com", "9307879687");
        phoneNumbers.put("Doctor_doctor@gmail.com", "9322067937");

        String key = userType + "_" + email;
        return phoneNumbers.getOrDefault(key, "");
    }

    private void saveStudentDetailsToPreferences(String enrollmentNo, String name, String email, String department, String year, String mobile, String emergencyContact, String position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("enrollmentNo", enrollmentNo);
        editor.putString("studentName", name);
        editor.putString("studentEmail", email);
        editor.putString("studentDepartment", department);
        editor.putString("studentYear", year);
        editor.putString("studentMobile", mobile);
        editor.putString("studentEmergencyContact", emergencyContact);
        editor.putString("studentPosition", position);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        // 🔹 Debug: Print stored values
        Log.d("DEBUG_PREFS", "Saved studentName: " + name);
        Log.d("DEBUG_PREFS", "Saved studentEmail: " + email);
        Log.d("DEBUG_PREFS", "Saved studentMobile: " + mobile);
    }




}
