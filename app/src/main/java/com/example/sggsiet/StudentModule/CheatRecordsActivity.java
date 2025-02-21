package com.example.sggsiet.StudentModule;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

public class CheatRecordsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView backButton;
    private CheatRecordAdapter adapter;
    private List<CheatRecord> cheatRecords;
    private DatabaseReference cheatRecordsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat_records);

        recyclerView = findViewById(R.id.recyclerViewCheatRecords);
        backButton = findViewById(R.id.m_back);

        cheatRecords = new ArrayList<>();
        adapter = new CheatRecordAdapter(this, cheatRecords);

        // ✅ Fix: Set LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Handle back button click
        backButton.setOnClickListener(v -> onBackPressed());

        // Reference to Firebase
        cheatRecordsRef = FirebaseDatabase.getInstance().getReference("CheatRecords");

        loadCheatRecords();
    }

    private void loadCheatRecords() {
        cheatRecordsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cheatRecords.clear();

                 // Loop through "210", "465"
                    for (DataSnapshot recordSnapshot : snapshot.getChildren()) { // Loop through actual records
                        if (recordSnapshot.exists() && recordSnapshot.hasChildren()) { // ✅ Ensure it's an object
                            CheatRecord record = recordSnapshot.getValue(CheatRecord.class);
                            if (record != null) {
                                cheatRecords.add(record);
                            }
                        } else {
                            Log.e("Firebase", "Skipping invalid record: " + recordSnapshot.getKey());
                        }

                }

                if (cheatRecords.isEmpty()) {
                    Toast.makeText(CheatRecordsActivity.this, "No records found", Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching data", error.toException());
            }
        });
    }

}
