package com.example.sggsiet.AdminModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import com.google.firebase.auth.FirebaseAuth;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.sggsiet.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadData extends AppCompatActivity {

    private static final int PICK_STUDENT_FILE_REQUEST = 1;
    private static final int PICK_FACULTY_FILE_REQUEST = 2;

    private TextInputEditText studentFileInput;
    private TextInputEditText facultyFileInput;
    private Uri studentFileUri;
    private Uri facultyFileUri;
    private ProgressDialog progressDialog;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_data);

        studentFileInput = findViewById(R.id.studentFile);
        facultyFileInput = findViewById(R.id.facultyFile);
        Button uploadStudentButton = findViewById(R.id.uploadstudent);
        Button uploadFacultyButton = findViewById(R.id.uploadfacult);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        studentFileInput.setOnClickListener(v -> openFileChooser(PICK_STUDENT_FILE_REQUEST));
        facultyFileInput.setOnClickListener(v -> openFileChooser(PICK_FACULTY_FILE_REQUEST));

        uploadStudentButton.setOnClickListener(v -> uploadFile(studentFileUri, "students"));
        uploadFacultyButton.setOnClickListener(v -> uploadFile(facultyFileUri, "faculty"));
    }

    private void openFileChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_STUDENT_FILE_REQUEST) {
                studentFileUri = data.getData();
                studentFileInput.setText(studentFileUri.getLastPathSegment());
            } else if (requestCode == PICK_FACULTY_FILE_REQUEST) {
                facultyFileUri = data.getData();
                facultyFileInput.setText(facultyFileUri.getLastPathSegment());
            }
        }
    }



    private void uploadFile(Uri fileUri, String folder) {
        if (fileUri != null) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() == null) {
                Toast.makeText(this, "Please log in to upload files", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = auth.getCurrentUser().getUid();
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference fileReference = storageReference.child("uploads/" + userId + "/" + folder + "/" + System.currentTimeMillis() + "_" + fileUri.getLastPathSegment());
            fileReference.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(UploadData.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(UploadData.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%...");
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

}