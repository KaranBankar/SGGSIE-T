package com.example.sggsiet.StudentModule;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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

public class ComplaintActivity extends AppCompatActivity {

    private RecyclerView recyclerViewComplaints;
    private ComplaintAdapter complaintAdapter;
    private List<Complaint> complaintList;
    private DatabaseReference databaseReference;


    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        recyclerViewComplaints = findViewById(R.id.recyclerViewComplaints);
        complaintList = new ArrayList<>();
        complaintAdapter = new ComplaintAdapter(this, complaintList);
        recyclerViewComplaints.setAdapter(complaintAdapter);

        backButton=findViewById(R.id.m_menu);
        // Handle back button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("complaints");
        fetchComplaints();
    }

    private void fetchComplaints() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                complaintList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Complaint complaint = dataSnapshot.getValue(Complaint.class);
                    complaintList.add(complaint);
                }
                complaintAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ComplaintActivity.this, "Failed to load complaints", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
