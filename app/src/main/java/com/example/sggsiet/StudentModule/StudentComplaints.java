package com.example.sggsiet.StudentModule;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sggsiet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.UUID;

public class StudentComplaints extends AppCompatActivity {
    private Spinner spinnerComplaintType;
    private EditText etTitle, etDescription;
    private ImageView ivSelectedImage;
    private Button btnChooseImage, btnCaptureImage, btnSubmit,btnViewComplaints;
    private Uri selectedImageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    private static final int PICK_IMAGE = 1;
    private static final int CAPTURE_IMAGE = 2;
    private static final int PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_complaints);

        // Initialize Views
        spinnerComplaintType = findViewById(R.id.spinner_complaint_type);
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        ivSelectedImage = findViewById(R.id.iv_selected_image);
        btnChooseImage = findViewById(R.id.btn_choose_image);
        btnCaptureImage = findViewById(R.id.btn_capture_image);
        btnSubmit = findViewById(R.id.btn_submit);
        btnViewComplaints = findViewById(R.id.btn_view_complaints);


        btnViewComplaints.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentComplaints.this, ComplaintActivity.class);
                startActivity(intent);
            }
        });
        storageReference = FirebaseStorage.getInstance().getReference("complaint_images");
        databaseReference = FirebaseDatabase.getInstance().getReference("complaints");

        // Initialize spinner data
        String[] complaintTypes = {"Infrastructure", "Faculty", "Hostel", "Library", "Canteen"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, complaintTypes);
        spinnerComplaintType.setAdapter(adapter);

        btnChooseImage.setOnClickListener(v -> checkPermissions(PICK_IMAGE));
        btnCaptureImage.setOnClickListener(v -> checkPermissions(CAPTURE_IMAGE));
        btnSubmit.setOnClickListener(v -> uploadComplaintData());
    }

    // ✅ Request Permissions if Needed
    private void checkPermissions(int requestType) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_CODE);
        } else {
            if (requestType == PICK_IMAGE) selectImageFromGallery();
            else captureImageFromCamera();
        }
    }

    // ✅ Open Gallery
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    // ✅ Capture Image
    private void captureImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    // ✅ Handle Permission Request Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ✅ Handle Image Selection and Camera Capture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
                selectedImageUri = data.getData();
                ivSelectedImage.setImageURI(selectedImageUri);
                ivSelectedImage.setVisibility(View.VISIBLE);
            } else if (requestCode == CAPTURE_IMAGE && data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                selectedImageUri = getImageUriFromBitmap(bitmap);
                ivSelectedImage.setImageBitmap(bitmap);
                ivSelectedImage.setVisibility(View.VISIBLE);
            }
        }
    }

    // ✅ Convert Bitmap to URI
    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "ComplaintImage", null);
        return Uri.parse(path);
    }

    // ✅ Upload Data to Firebase
    private void uploadComplaintData() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String complaintType = spinnerComplaintType.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting complaint...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (selectedImageUri != null) {
            uploadImageToFirebase(title, description, complaintType);
        } else {
            saveComplaintToDatabase(title, description, complaintType, null);
        }
    }

    // ✅ Upload Image to Firebase Storage
    private void uploadImageToFirebase(String title, String description, String complaintType) {
        StorageReference fileRef = storageReference.child(UUID.randomUUID().toString() + ".jpg");

        fileRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveComplaintToDatabase(title, description, complaintType, uri.toString());
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss(); // Dismiss dialog on failure
                    Toast.makeText(StudentComplaints.this, "Image Upload Failed!", Toast.LENGTH_SHORT).show();
                });
    }

    // ✅ Save Complaint Data to Firebase Database
    private void saveComplaintToDatabase(String title, String description, String complaintType, String imageUrl) {
        String complaintId = databaseReference.push().getKey();

        HashMap<String, Object> complaintData = new HashMap<>();
        complaintData.put("complaintId", complaintId);
        complaintData.put("title", title);
        complaintData.put("description", description);
        complaintData.put("complaintType", complaintType);
        complaintData.put("imageUrl", imageUrl);

        databaseReference.child(complaintId).setValue(complaintData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(StudentComplaints.this, "Complaint Submitted!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(StudentComplaints.this, "Submission Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
