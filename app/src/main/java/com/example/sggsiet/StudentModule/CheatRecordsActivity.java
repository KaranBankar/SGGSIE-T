package com.example.sggsiet.StudentModule;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
        cheatRecords = new ArrayList<>();
        adapter = new CheatRecordAdapter(this, cheatRecords);
        recyclerView.setAdapter(adapter);

        backButton=findViewById(R.id.m_back);
        // Handle back button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        cheatRecordsRef = FirebaseDatabase.getInstance().getReference("CheatRecords");

        loadCheatRecords();
    }

    private void loadCheatRecords() {
        cheatRecordsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cheatRecords.clear();
                for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                    CheatRecord record = recordSnapshot.getValue(CheatRecord.class);
                    if (record != null) {
                        cheatRecords.add(record);
                    }
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
