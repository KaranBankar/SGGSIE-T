package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VotingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VotecardAdapter votecardAdapter;
    private List<String> positionList;
    private DatabaseReference databaseReference;

    private MaterialCardView appliedPositionCard;
    private TextView appliedPositionName;
    private MaterialButton buttonCancelApplication;

    private String userEnrollmentNo, userName, userEmail;
    private MaterialButton votingpage;
    private String appliedPosition = null; // Stores applied position name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voting);

        recyclerView = findViewById(R.id.showpositions);
        appliedPositionCard = findViewById(R.id.appliedPositionCard);
        appliedPositionName = findViewById(R.id.textViewAppliedPositionName);
        buttonCancelApplication = findViewById(R.id.buttonCancelApplication);
        votingpage=findViewById(R.id.votenow);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        positionList = new ArrayList<>();
        votecardAdapter = new VotecardAdapter(this, positionList, this::onPositionApplied);
        recyclerView.setAdapter(votecardAdapter);

        // Fetch user details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userEnrollmentNo = sharedPreferences.getString("enrollmentNo", "");
        userName = sharedPreferences.getString("studentName", "");
        userEmail = sharedPreferences.getString("studentEmail", "");

        databaseReference = FirebaseDatabase.getInstance().getReference("SGGSIE&T/ElectionPositions");

        checkUserApplication();
        votingpage.setOnClickListener(v->{
            startActivity(new Intent(this,VotingPage.class));
        });
    }

    /**
     * Checks if the user has already applied for a position and updates UI accordingly.
     */
    private void checkUserApplication() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("SGGSIE&T/AppliedCandidates/" + userEnrollmentNo);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    appliedPosition = snapshot.getValue(String.class);
                    showAppliedPositionUI(appliedPosition);
                } else {
                    fetchPositions(); // Show available positions
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());
            }
        });
    }

    /**
     * Fetches the list of available positions from Firebase and updates RecyclerView.
     */
    private void fetchPositions() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                positionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String facilityName = snapshot.getValue(String.class);
                    positionList.add(facilityName);
                }
                votecardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Called when the user applies for a position.
     */
    private void onPositionApplied(String positionName) {
        appliedPosition = positionName;

        DatabaseReference appliedRef = FirebaseDatabase.getInstance()
                .getReference("SGGSIE&T/AppliedCandidates/" + userEnrollmentNo);
        appliedRef.setValue(positionName);

        // Ensure the correct structure is maintained under "AppliedPositions"
        DatabaseReference positionRef = FirebaseDatabase.getInstance()
                .getReference("SGGSIE&T/AppliedPositions/" + positionName + "/" + userEnrollmentNo);

        CandidateModel candidate = new CandidateModel(userName, userEmail, userEnrollmentNo);

        positionRef.setValue(candidate) // Store the candidate as an object
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Candidate applied successfully!"))
                .addOnFailureListener(e -> Log.e("FirebaseError", "Error storing data: " + e.getMessage()));

        showAppliedPositionUI(positionName);
    }

    /**
     * Updates UI to show the applied position card and hide the RecyclerView.
     */
    private void showAppliedPositionUI(String positionName) {
        recyclerView.setVisibility(View.GONE);
        appliedPositionCard.setVisibility(View.VISIBLE);
        appliedPositionName.setText(positionName);

        buttonCancelApplication.setOnClickListener(v -> cancelApplication());
    }

    /**
     * Cancels the user's application, removes the record from Firebase, and shows available positions.
     */
    private void cancelApplication() {
        if (appliedPosition != null) {
            DatabaseReference appliedRef = FirebaseDatabase.getInstance().getReference("SGGSIE&T/AppliedCandidates/" + userEnrollmentNo);
            appliedRef.removeValue();

            DatabaseReference positionRef = FirebaseDatabase.getInstance().getReference("SGGSIE&T/AppliedPositions/" + appliedPosition + "/" + userEnrollmentNo);
            positionRef.removeValue();

            appliedPosition = null;
            appliedPositionCard.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            fetchPositions();
            Toast.makeText(this, "Application Cancelled!", Toast.LENGTH_SHORT).show();
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed(); // Go back when clicking back arrow
        });
    }


}
