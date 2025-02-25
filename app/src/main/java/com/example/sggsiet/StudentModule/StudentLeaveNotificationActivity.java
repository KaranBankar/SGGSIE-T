package com.example.sggsiet.StudentModule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sggsiet.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class StudentLeaveNotificationActivity extends AppCompatActivity {

    EditText etDuration, etReason;
    Button btnSubmit;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_leave_notification);

        etDuration = findViewById(R.id.etDuration);
        etReason = findViewById(R.id.etReason);
        btnSubmit = findViewById(R.id.btnSubmit);
        databaseReference = FirebaseDatabase.getInstance().getReference("LeaveRequests");
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        btnSubmit.setOnClickListener(view -> submitLeaveRequest());
    }

    private void submitLeaveRequest() {
        String duration = etDuration.getText().toString().trim();
        String reason = etReason.getText().toString().trim();

        if (duration.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch student details from SharedPreferences
        String studentEmail = sharedPreferences.getString("studentEmail", "N/A");
        String studentName = sharedPreferences.getString("studentName", "N/A");
        String studentMobile = sharedPreferences.getString("studentMobile", "N/A");
        String studentDept = sharedPreferences.getString("studentDept", "N/A");

        // 🔥 Added 'status' field with default value 'pending'
        HashMap<String, String> leaveData = new HashMap<>();
        leaveData.put("studentName", studentName);
        leaveData.put("studentEmail", studentEmail);
        leaveData.put("studentMobile", studentMobile);
        leaveData.put("studentDept", studentDept);
        leaveData.put("duration", duration);
        leaveData.put("reason", reason);
        leaveData.put("status", "pending");  // ✅ Added status field

        // Faculty, Warden & Parent emails
        String[] recipientEmails = {"karanbankar54@gmail.com", "dsjoshi13@gmail.com", "auteshravani@gmail.com"};
        leaveData.put("facultyEmail", recipientEmails[0]);
        leaveData.put("wardenEmail", recipientEmails[1]);
        leaveData.put("parentEmail", recipientEmails[2]);

        databaseReference.push().setValue(leaveData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Leave request submitted successfully!", Toast.LENGTH_SHORT).show();
                    sendEmail(recipientEmails, studentName, studentDept, duration, reason);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to submit leave request.", Toast.LENGTH_SHORT).show());
    }

    private void sendEmail(String[] recipientEmails, String name, String dept, String duration, String reason) {
        String subject = "Leave Request Notification from " + name;
        String message = "Student Name: " + name + "\n"
                + "Department: " + dept + "\n"
                + "Duration (Days): " + duration + "\n"
                + "Reason: " + reason + "\n\n"
                + "This is an automated message for leave approval.";

        // Using ACTION_SENDTO with mailto: URI
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipientEmails);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
