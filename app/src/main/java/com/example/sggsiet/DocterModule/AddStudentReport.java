package com.example.sggsiet.DocterModule;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sggsiet.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddStudentReport extends AppCompatActivity {

    private TextInputEditText etStudentEmail, etHealthIssue, etDescription;
    private MaterialButton btnSubmitComplaint;
    private DatabaseReference complaintsRef;
    private StorageReference csvStorageRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student_report);

        etStudentEmail = findViewById(R.id.etStudentEmail);
        etHealthIssue = findViewById(R.id.etHealthIssue);
        etDescription = findViewById(R.id.etDescription);
        btnSubmitComplaint = findViewById(R.id.btnSubmitComplaint);

        complaintsRef = FirebaseDatabase.getInstance().getReference("SGGSIE&T").child("healthreports");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


            csvStorageRef = FirebaseStorage.getInstance().getReference("dummy_student_data.csv");

            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();


        btnSubmitComplaint.setOnClickListener(v -> checkStudentAndSubmit());
    }

    private void checkStudentAndSubmit() {
        String email = etStudentEmail.getText().toString().trim();
        String healthIssue = etHealthIssue.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (email.isEmpty() || healthIssue.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching Student...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        csvStorageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.ByteArrayInputStream(bytes)));
                String line;
                boolean found = false;
                HashMap<String, String> studentDetails = new HashMap<>();

                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length < 8) continue;

                    if (parts[2].equalsIgnoreCase(email)) {
                        studentDetails.put("name", parts[1]);
                        studentDetails.put("email", parts[2]);
                        studentDetails.put("enrollmentno", parts[0]);
                        studentDetails.put("department", parts[3]);
                        studentDetails.put("year", parts[4]);
                        studentDetails.put("studentMobile", parts[5]); // Storing student mobile
                        studentDetails.put("position", parts[7]);
                        found = true;
                        break;
                    }
                }

                progressDialog.dismiss();

                if (found) {
                    showSubmissionDialog(studentDetails, healthIssue, description);
                } else {
                    showErrorDialog("Student Not Found", "Please enter a valid student email.");
                }

            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(this, "Error reading CSV", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Failed to fetch student data", Toast.LENGTH_SHORT).show();
        });
    }

    private void showSubmissionDialog(HashMap<String, String> studentDetails, String healthIssue, String description) {
        ProgressDialog submitProgressDialog = new ProgressDialog(this);
        submitProgressDialog.setMessage("Submitting Health Report...");
        submitProgressDialog.setCancelable(false);
        submitProgressDialog.show();

        submitComplaint(studentDetails, healthIssue, description, submitProgressDialog);
    }

    private void submitComplaint(HashMap<String, String> studentDetails, String healthIssue, String description, ProgressDialog submitProgressDialog) {
        String complaintId = complaintsRef.push().getKey();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        HashMap<String, Object> complaint = new HashMap<>();
        complaint.putAll(studentDetails);
        complaint.put("healthissue", healthIssue);
        complaint.put("description", description);
        complaint.put("currentdate", currentDate);
        complaint.put("currenttime", currentTime);
        complaint.put("treatment", "Pending");
        complaint.put("location", "SGGSIE&T");

        complaintsRef.child(complaintId).setValue(complaint)
                .addOnSuccessListener(aVoid -> {
                    submitProgressDialog.dismiss();
                    showSuccessDialog();
                })
                .addOnFailureListener(e -> {
                    submitProgressDialog.dismiss();
                    Toast.makeText(this, "Failed to submit complaint", Toast.LENGTH_SHORT).show();
                });
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Complaint submitted successfully!")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}
