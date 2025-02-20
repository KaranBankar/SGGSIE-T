package com.example.sggsiet.StudentModule;

import android.os.Bundle;
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VotingPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PositionAdapterForvote positionAdapter;
    private Map<String, List<CandidateModel>> positionMap;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_page);

        recyclerView = findViewById(R.id.recyclerViewCandidates);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        positionMap = new HashMap<>();
        positionAdapter = new PositionAdapterForvote(this, positionMap);
        recyclerView.setAdapter(positionAdapter); // Set adapter before fetching data

        databaseReference = FirebaseDatabase.getInstance().getReference("SGGSIE&T/AppliedPositions");

        fetchAppliedCandidates();
    }

    private void fetchAppliedCandidates() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                positionMap.clear();

                if (!snapshot.exists()) {
                    Toast.makeText(VotingPage.this, "No data found in Firebase!", Toast.LENGTH_LONG).show();
                    Log.e("FirebaseDebug", "No data found at AppliedPositions.");
                    return;
                }

                for (DataSnapshot positionSnapshot : snapshot.getChildren()) {
                    String positionName = positionSnapshot.getKey(); // e.g., "College President"
                    Toast.makeText(VotingPage.this, "Fetching: " + positionName, Toast.LENGTH_SHORT).show();
                    Log.d("FirebaseDebug", "Position Found: " + positionName);

                    List<CandidateModel> candidateList = new ArrayList<>();

                    for (DataSnapshot candidateSnapshot : positionSnapshot.getChildren()) {
                        // Fetch each candidate inside "BT24S05F002"
                        String candidateName = candidateSnapshot.child("name").getValue(String.class);
                        if (candidateName != null) {
                            CandidateModel candidate = new CandidateModel();
                            candidate.setName(candidateName);
                            candidateList.add(candidate);

                            Log.d("FirebaseDebug", "Candidate Found: " + candidateName);
                        } else {
                            Log.e("FirebaseDebug", "Candidate data missing for: " + candidateSnapshot.getKey());
                        }
                    }

                    positionMap.put(positionName, candidateList);
                }

                if (positionMap.isEmpty()) {
                    Toast.makeText(VotingPage.this, "No candidates found!", Toast.LENGTH_LONG).show();
                    Log.e("FirebaseDebug", "No candidates found.");
                } else {
                    Toast.makeText(VotingPage.this, "Data Loaded Successfully!", Toast.LENGTH_SHORT).show();
                }

                // ✅ Correctly update RecyclerView
                positionAdapter.updateData(positionMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());
                Toast.makeText(VotingPage.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
