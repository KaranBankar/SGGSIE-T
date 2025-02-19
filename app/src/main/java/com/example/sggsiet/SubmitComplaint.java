package com.example.sggsiet;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;

import java.io.IOException;

public class SubmitComplaint extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_PERMISSION = 100;

    private ImageView selectedImageView;
    private Uri imageUri;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_complaint);
        FirebaseApp.initializeApp(this);

        selectedImageView = findViewById(R.id.selected_image);
        MaterialButton chooseFileButton = findViewById(R.id.choosefile);

        chooseFileButton.setOnClickListener(v -> requestPermissionsAndOpenChooser());
    }

    private void requestPermissionsAndOpenChooser() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    REQUEST_PERMISSION);
        } else {
            openImageChooser();
        }
    }

    private void openImageChooser() {
        String[] options = {"Choose from Gallery", "Take a New Picture"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        pickImageFromGallery();
                    } else if (which == 1) {
                        captureImageFromCamera();
                    }
                })
                .show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void captureImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                imageUri = data.getData();
                showImageDialog();
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data.getExtras() != null) {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                showImageDialog();
            }
        }
    }

    private void showImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selected Image");

        ImageView imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);

        if (imageUri != null) {
            imageView.setImageURI(imageUri);
        } else if (imageBitmap != null) {
            imageView.setImageBitmap(imageBitmap);
        }

        builder.setView(imageView);

        builder.setPositiveButton("Retake", (dialog, which) -> openImageChooser());
        builder.setNegativeButton("Done", (dialog, which) -> {
            dialog.dismiss();

            // Set the image in ImageView
            if (imageUri != null) {
                selectedImageView.setImageURI(imageUri);
                selectedImageView.setVisibility(View.VISIBLE);
            } else if (imageBitmap != null) {
                selectedImageView.setImageBitmap(imageBitmap);
                selectedImageView.setVisibility(View.VISIBLE);
            }
        });

        builder.show();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            boolean allPermissionsGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                openImageChooser();  // Proceed to open image selector
            } else {
                boolean shouldShowRationale = false;
                for (String permission : permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        shouldShowRationale = true;
                        break;
                    }
                }

                if (shouldShowRationale) {
                    // Ask again with explanation
                    new AlertDialog.Builder(this)
                            .setTitle("Permissions Needed")
                            .setMessage("This app needs access to your storage and camera to upload images.")
                            .setPositiveButton("Grant", (dialog, which) -> requestPermissionsAndOpenChooser())
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    // Permissions permanently denied, show settings dialog
                    showPermissionDeniedDialog();
                }
            }
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("To upload an image, you need to allow access in settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }


}
