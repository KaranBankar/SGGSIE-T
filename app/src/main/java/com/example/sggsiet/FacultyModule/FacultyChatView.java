package com.example.sggsiet.FacultyModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FacultyChatView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FacultyChatAdapter adapter;
    private List<ChatMessage> chatList;
    private DatabaseReference chatRef;
    private String facultyName, studentName, studentEmail;
    private EditText messageInput;
    private ImageView sendButton, attachImageButton, previewImage;
    private Uri selectedImageUri;
    private ProgressDialog progressDialog;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_FACULTY_NAME = "facultyName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_chat_view);

        recyclerView = findViewById(R.id.recyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        attachImageButton = findViewById(R.id.attachImageButton);
        previewImage = findViewById(R.id.previewImage);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<>();
        adapter = new FacultyChatAdapter(this, chatList);
        recyclerView.setAdapter(adapter);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        facultyName = sharedPreferences.getString(KEY_FACULTY_NAME, "Faculty").replace(".", "_");

        Intent intent = getIntent();
        studentName = intent.getStringExtra("studentName");
        studentEmail = intent.getStringExtra("studentEmail");

        chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(facultyName);
        fetchChatMessages();

        sendButton.setOnClickListener(v -> sendMessage());
        attachImageButton.setOnClickListener(v -> openImageChooser());
    }

    private void fetchChatMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    ChatMessage message = chatSnapshot.getValue(ChatMessage.class);
                    if (message != null && message.getEmail().equals(studentEmail)) {
                        chatList.add(message);
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatList.size() - 1); // Auto-scroll to bottom
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseChat", "Failed to load chat: " + error.getMessage());
                Toast.makeText(FacultyChatView.this, "Failed to load chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (messageText.isEmpty() && selectedImageUri == null) {
            Toast.makeText(this, "Enter a message or select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference messageRef = chatRef.push();
        String messageId = messageRef.getKey();
        long timestamp = System.currentTimeMillis();

        if (selectedImageUri != null) {
            progressDialog.show(); // Show progress dialog while uploading image
            StorageReference imageRef = FirebaseStorage.getInstance().getReference("ChatImages").child(messageId + ".jpg");
            imageRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot ->
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        ChatMessage message = new ChatMessage(studentName, studentEmail, messageText, uri.toString(), timestamp);
                        messageRef.setValue(message);
                        previewImage.setVisibility(View.GONE);
                        selectedImageUri = null;
                        progressDialog.dismiss(); // Hide progress dialog
                    })
            ).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(FacultyChatView.this, "Image upload failed", Toast.LENGTH_SHORT).show();
            });
        } else {
            ChatMessage message = new ChatMessage(studentName, studentEmail, messageText, null, timestamp);
            messageRef.setValue(message);
        }

        messageInput.setText("");
        recyclerView.scrollToPosition(chatList.size() - 1); // Auto-scroll to bottom
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            previewImage.setImageURI(selectedImageUri);
            previewImage.setVisibility(View.VISIBLE);
        }
    }
}
