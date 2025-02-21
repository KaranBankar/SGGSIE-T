package com.example.sggsiet.StudentModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sggsiet.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class BudgetRequestSubmission extends AppCompatActivity {

    private TextInputEditText etRequestTitle, etRequestDescription,funds;
    private Spinner spinnerRequestType;
    private MaterialCheckBox checkDeclaration;
    private MaterialButton btnUploadFile, btnSubmitRequest;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri fileUri;
    private static final int FILE_PICKER_REQUEST = 1;
    private ProgressDialog progressDialog;
    private String selectedRequestType = ""; // To store selected request type
    private String enrollmentNo, studentName; // To store student details from SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_request_submission);

        // Firebase setup
        databaseReference = FirebaseDatabase.getInstance().getReference("BudgetRequests");
        storageReference = FirebaseStorage.getInstance().getReference("Attachments");

        // UI Elements
        spinnerRequestType = findViewById(R.id.spinnerRequestType);
        etRequestTitle = findViewById(R.id.etRequestTitle);
        etRequestDescription = findViewById(R.id.etRequestDescription);
        checkDeclaration = findViewById(R.id.checkDeclaration);
        btnUploadFile = findViewById(R.id.btnUploadFile);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        funds=findViewById(R.id.fundsamount);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting request...");

        // Retrieve student details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        enrollmentNo = sharedPreferences.getString("enrollmentNo", ""); // Default is empty
        studentName = sharedPreferences.getString("studentName", ""); // Default is empty

        if (TextUtils.isEmpty(enrollmentNo) || TextUtils.isEmpty(studentName)) {
            Toast.makeText(this, "User details not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize spinner with options
        String[] requestTypes = { "Event Budget", "Sponsorship", "Equipment Purchase", "Travel Grant", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, requestTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRequestType.setAdapter(adapter);

        // Handle spinner selection
        spinnerRequestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRequestType = requestTypes[position]; // Get selected request type
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRequestType = "";
            }
        });

        // File upload button
        btnUploadFile.setOnClickListener(v -> pickFile());

        // Submit button
        btnSubmitRequest.setOnClickListener(v -> submitRequest());
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            Toast.makeText(this, "File selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitRequest() {
        String requestType = selectedRequestType.trim();
        String title = Objects.requireNonNull(etRequestTitle.getText()).toString().trim();
        String description = Objects.requireNonNull(etRequestDescription.getText()).toString().trim();
        String fundsamount = Objects.requireNonNull(funds.getText()).toString().trim();


        // Validation
        if (requestType.equals("Select Request Type") || TextUtils.isEmpty(requestType) || TextUtils.isEmpty(title) || TextUtils.isEmpty(description)||TextUtils.isEmpty(fundsamount)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!checkDeclaration.isChecked()) {
            Toast.makeText(this, "Please confirm the declaration", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        String requestId = databaseReference.push().getKey();
        String status = "Pending"; // Default status

        // Handle file upload if selected
        if (fileUri != null) {
            StorageReference fileRef = storageReference.child(requestId + "_" + System.currentTimeMillis());
            fileRef.putFile(fileUri).addOnSuccessListener(taskSnapshot ->
                            fileRef.getDownloadUrl().addOnSuccessListener(uri ->
                                    saveRequest(requestId, requestType, title, description,fundsamount, uri.toString(), status)))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "File upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            saveRequest(requestId, requestType, title, description, fundsamount,"", status);
        }
    }

    private void saveRequest(String requestId, String requestType, String title, String description,String fundsamount, String attachmentUrl, String status) {
        BudgetRequest request = new BudgetRequest(requestId, enrollmentNo, studentName, requestType, title, description,fundsamount, attachmentUrl, status, System.currentTimeMillis());
        databaseReference.child(requestId).setValue(request).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            Toast.makeText(this, task.isSuccessful() ? "Request submitted successfully" : "Submission failed", Toast.LENGTH_SHORT).show();
            if (task.isSuccessful()) finish();
        });
    }
}
