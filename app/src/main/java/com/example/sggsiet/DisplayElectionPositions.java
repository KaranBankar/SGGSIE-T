package com.example.sggsiet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.Adapter.PositionAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisplayElectionPositions extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PositionAdapter adapter;
    private ArrayList<String> positionList;
    private DatabaseReference databaseReference;

    private final String[] defaultPositions = {
            "College President", "Sports Coordinator", "Cultural Secretary",
            "Treasurer", "General Secretary", "Technical Head"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_election_positions);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("SGGSIE&T").child("ElectionPositions");

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewPositions);
        MaterialButton btnAddPosition = findViewById(R.id.btnAddPosition);
        MaterialButton btnPostPositions = findViewById(R.id.btnPostPositions);

        // Setup list with default positions
        positionList = new ArrayList<>();
        for (String pos : defaultPositions) {
            positionList.add(pos);
        }

        // Setup RecyclerView
        adapter = new PositionAdapter(positionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Add Position Button Click
        btnAddPosition.setOnClickListener(v -> showAddPositionDialog());

        // Post Positions to Firebase
        btnPostPositions.setOnClickListener(v -> postPositionsToFirebase());
    }

    private void showAddPositionDialog() {
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_position, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText editTextPosition = dialogView.findViewById(R.id.editTextPosition);
        MaterialButton btnDialogAdd = dialogView.findViewById(R.id.btnDialogAdd);

        btnDialogAdd.setOnClickListener(v -> {
            String newPosition = editTextPosition.getText().toString().trim();
            if (!newPosition.isEmpty()) {
                positionList.add(newPosition);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                editTextPosition.setError("Enter position name");
            }
        });
    }

    private void postPositionsToFirebase() {
        // Show loading dialog
        AlertDialog loadingDialog = new MaterialAlertDialogBuilder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_loading, null))
                .setCancelable(false)
                .create();
        loadingDialog.show();

        // Prepare data for Firebase
        Map<String, Object> positionsMap = new HashMap<>();
        for (int i = 0; i < positionList.size(); i++) {
            positionsMap.put("position_" + (i + 1), positionList.get(i));
        }

        // Upload to Firebase
        databaseReference.setValue(positionsMap)
                .addOnSuccessListener(unused -> {
                    loadingDialog.dismiss(); // Dismiss loading
                    Toast.makeText(this, "Positions Posted Successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss(); // Dismiss loading
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
