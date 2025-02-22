package com.example.sggsiet.StudentModule;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageView selectImageBtn;
    private Button sendBtn;

    private  String studentName,studentEmail;

    private static final int PICK_IMAGE = 1;
    private Uri imageUri;

    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private DatabaseReference chatRef;
    private StorageReference storageRef;

    private String facultyName;
    private ProgressDialog progressDialog;

    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_STUDENT_NAME = "studentName";
// ProgressDialog for showing loading

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        sendBtn = findViewById(R.id.sendBtn);

        Intent intent = getIntent();
        facultyName = intent.getStringExtra("faculty_name");
        // Fetch actual student name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        studentName = sharedPreferences.getString(KEY_STUDENT_NAME, "Student");
        studentEmail = sharedPreferences.getString("studentEmail", "Student");
        String safeFacultyName = facultyName.replace(".", "_"); // Replace dots with underscores
        chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(safeFacultyName);
        storageRef = FirebaseStorage.getInstance().getReference("ChatImages");

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending image...");
        progressDialog.setCancelable(false);

        loadChatMessages();

        sendBtn.setOnClickListener(v -> sendMessage());

        selectImageBtn.setOnClickListener(v -> selectImage());
    }

    private void loadChatMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Message msg = data.getValue(Message.class);

                    if (msg != null && msg.getEmail() != null && msg.getEmail().equals(studentEmail)) {
                        messageList.add(msg); // Add only messages of the logged-in student
                    }
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseChat", "Failed to load messages: " + error.getMessage());
            }
        });
    }


    private void sendMessage() {
        String text = messageInput.getText().toString().trim();

        if (text.isEmpty() && imageUri == null) {
            Toast.makeText(this, "Enter a message or select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String key = chatRef.push().getKey();

        if (imageUri != null) {
            uploadImageToFirebase(key, text);
        } else {
            chatRef.child(key).setValue(new Message(text, null, studentName, new Date().getTime(),studentEmail));
            messageInput.setText("");  // Clear text input after sending
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void uploadImageToFirebase(String key, String text) {
        progressDialog.show();  // Show ProgressDialog

        StorageReference imageRef = storageRef.child(key + ".jpg");

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    chatRef.child(key).setValue(new Message(text, uri.toString(), studentName, new Date().getTime(),studentEmail));
                    progressDialog.dismiss();  // Hide ProgressDialog
                    imageUri = null;
                    messageInput.setText("");  // Clear text input
                    Toast.makeText(this, "Image Sent", Toast.LENGTH_SHORT).show();
                })
        ).addOnFailureListener(e -> {
            progressDialog.dismiss();  // Hide ProgressDialog if upload fails
            Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
        }
    }
}
