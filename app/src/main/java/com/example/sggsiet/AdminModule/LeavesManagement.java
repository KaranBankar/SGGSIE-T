package com.example.sggsiet.AdminModule;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sggsiet.R;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class LeavesManagement extends AppCompatActivity implements LeaveManagementAdapter.OnLeaveActionListener {

    private RecyclerView recyclerView;
    private LeaveManagementAdapter adapter;
    private List<LeaveApplication> leaveList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leaves_management);

        recyclerView = findViewById(R.id.recyclerViewLeaves);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        leaveList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("leaveApplications");

        adapter = new LeaveManagementAdapter(leaveList, this);
        recyclerView.setAdapter(adapter);

        fetchAllLeaveApplications();
    }

    private void fetchAllLeaveApplications() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leaveList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    LeaveApplication application = dataSnapshot.getValue(LeaveApplication.class);
                    if (application != null) {
                        leaveList.add(application);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LeavesManagement.this, "Failed to load leaves: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onLeaveAction(LeaveApplication leave, String newStatus) {
        databaseReference.orderByChild("email").equalTo(leave.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String leaveId = dataSnapshot.getKey(); // Get the unique key
                        DatabaseReference leaveRef = databaseReference.child(leaveId);

                        // Update the status
                        leaveRef.child("status").setValue(newStatus)
                                .addOnSuccessListener(aVoid -> {
                                    leave.setStatus(newStatus); // Update local object
                                    adapter.notifyDataSetChanged(); // Refresh RecyclerView
                                    Toast.makeText(LeavesManagement.this, "Leave " + newStatus, Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(LeavesManagement.this, "Failed to update leave: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    Toast.makeText(LeavesManagement.this, "Leave application not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LeavesManagement.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
