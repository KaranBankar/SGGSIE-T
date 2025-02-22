package com.example.sggsiet.FacultyModule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SolveDoubt extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SolveDoubtAdapter adapter;
    private List<StudentChat> studentList;
    private DatabaseReference chatRef;
    private String facultyName;
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_STUDENT_NAME = "facultyName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_doubt);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        studentList = new ArrayList<>();
        adapter = new SolveDoubtAdapter(this, studentList);
        recyclerView.setAdapter(adapter);

        // TODO: Fetch faculty name dynamically if needed
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        facultyName = sharedPreferences.getString(KEY_STUDENT_NAME, "Student");
        String safeFacultyName = facultyName.replace(".", "_"); // Replace dots with underscores



        chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(safeFacultyName);
        fetchStudentChats();
    }

    private void fetchStudentChats() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, StudentChat> uniqueStudents = new HashMap<>();

                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    String email = chatSnapshot.child("email").getValue(String.class);
                    String sender = chatSnapshot.child("sender").getValue(String.class);
                    String imageUrl = chatSnapshot.child("imageUrl").getValue(String.class);
                    long timestamp = chatSnapshot.child("timestamp").getValue(Long.class);

                    if (email != null && sender != null) {
                        // Store the latest chat per student
                        if (!uniqueStudents.containsKey(email) || timestamp > uniqueStudents.get(email).getTimestamp()) {
                            uniqueStudents.put(email, new StudentChat(sender, email, imageUrl, timestamp));
                        }
                    }
                }

                // Convert HashMap to List and sort by timestamp (latest first)
                studentList.clear();
                studentList.addAll(uniqueStudents.values());
                Collections.sort(studentList, (s1, s2) -> Long.compare(s2.getTimestamp(), s1.getTimestamp()));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SolveDoubt.this, "Failed to load students", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
