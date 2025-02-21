package com.example.sggsiet;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sggsiet.StudentModule.Event;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class UploadEvent extends AppCompatActivity {

    private TextInputEditText etEventName, etEventDescription, etEventDate, etEventTime, etEventLocation, etEventSeats;
    private ImageView ivEventImage;
    private MaterialButton btnUploadImage, btnUploadEvent;
    private Uri imageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_event);

        etEventName = findViewById(R.id.etEventName);
        etEventDescription = findViewById(R.id.etEventDescription);
        etEventDate = findViewById(R.id.etEventDate);
        etEventTime = findViewById(R.id.etEventTime);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventSeats = findViewById(R.id.etEventSeats);
        ivEventImage = findViewById(R.id.ivEventImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnUploadEvent = findViewById(R.id.btnUploadEvent);
        ImageButton btnBack = findViewById(R.id.btnBack);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Event...");
        progressDialog.setCancelable(false);

        databaseReference = FirebaseDatabase.getInstance().getReference("events");
        storageReference = FirebaseStorage.getInstance().getReference("event_images");

        btnBack.setOnClickListener(v -> finish());
        btnUploadImage.setOnClickListener(v -> checkStoragePermission());
        btnUploadEvent.setOnClickListener(v -> validateAndUploadEvent());

        etEventDate.setOnClickListener(v -> showDatePicker());
        etEventTime.setOnClickListener(v -> showTimePicker());
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_CODE);
            } else {
                pickImageFromGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else {
                pickImageFromGallery();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "Storage permission is required to select an image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    ivEventImage.setImageURI(imageUri);
                }
            }
    );

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                etEventDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) ->
                etEventTime.setText(hourOfDay + ":" + minute),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void validateAndUploadEvent() {
        String eventName = etEventName.getText().toString().trim();
        String eventDescription = etEventDescription.getText().toString().trim();
        String eventDate = etEventDate.getText().toString().trim();
        String eventTime = etEventTime.getText().toString().trim();
        String eventLocation = etEventLocation.getText().toString().trim();
        String eventSeats = etEventSeats.getText().toString().trim();

        if (eventName.isEmpty() || eventDescription.isEmpty() || eventDate.isEmpty() || eventTime.isEmpty() || eventLocation.isEmpty() || imageUri == null || eventSeats.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        uploadImageToFirebase(eventName, eventDescription, eventDate, eventTime, eventLocation, eventSeats);
    }

    private void uploadImageToFirebase(String name, String desc, String date, String time, String location, String seats) {
        progressDialog.show(); // ✅ Show progress dialog before uploading

        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + ".jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(uri ->
                                saveEventToDatabase(name, desc, date, time, location, uri.toString(), seats)
                        )
                )
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveEventToDatabase(String name, String desc, String date, String time, String location, String imageUrl, String seats) {
        String eventId = databaseReference.push().getKey();
        Event event = new Event(eventId, name, desc, date, time, location, imageUrl, "pending", seats, "0");

        databaseReference.child(eventId).setValue(event)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss(); // ✅ Hide progress dialog after database update
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Event uploaded successfully!", Toast.LENGTH_SHORT).show();
                        onBackPressed(); // ✅ Go back after successful upload
                    } else {
                        Toast.makeText(this, "Failed to upload event!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
