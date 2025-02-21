package com.example.sggsiet.StudentModule;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

    private RecyclerView recyclerViewCandidates, recyclerViewVotes;
    private PositionAdapterForvote positionAdapter;
    private DisplayVoteAdapter voteAdapter;
    private Map<String, List<CandidateModel>> positionMap;
    private List<String> positionList;
    private Map<String, Map<String, Integer>> votesMap;
    private DatabaseReference appliedCandidatesRef, votesRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_page);

        // Initialize RecyclerViews
        recyclerViewCandidates = findViewById(R.id.recyclerViewCandidates);
        recyclerViewCandidates.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewVotes = findViewById(R.id.recyclerViewVotes);
        recyclerViewVotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVotes.setVisibility(View.GONE);

        // Initialize Total Votes TextView


        // Initialize Maps and Adapters
        positionMap = new HashMap<>();
        positionAdapter = new PositionAdapterForvote(this, positionMap);
        recyclerViewCandidates.setAdapter(positionAdapter);

        positionList = new ArrayList<>();
        votesMap = new HashMap<>();
        voteAdapter = new DisplayVoteAdapter(this, positionList, votesMap);
        recyclerViewVotes.setAdapter(voteAdapter);

        // Firebase Database References
        appliedCandidatesRef = FirebaseDatabase.getInstance().getReference("SGGSIE&T/AppliedPositions");
        votesRef = FirebaseDatabase.getInstance().getReference("Votes");

        // Fetch Data from Firebase
        fetchAppliedCandidates();
        fetchTotalVotes();
    }

    private void fetchAppliedCandidates() {
        appliedCandidatesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                positionMap.clear();

                if (!snapshot.exists()) {
                    Toast.makeText(VotingPage.this, "No candidates found!", Toast.LENGTH_LONG).show();
                    return;
                }

                for (DataSnapshot positionSnapshot : snapshot.getChildren()) {
                    String positionName = positionSnapshot.getKey();
                    List<CandidateModel> candidateList = new ArrayList<>();

                    for (DataSnapshot candidateSnapshot : positionSnapshot.getChildren()) {
                        String candidateName = candidateSnapshot.child("name").getValue(String.class);
                        if (candidateName != null) {
                            CandidateModel candidate = new CandidateModel();
                            candidate.setName(candidateName);
                            candidateList.add(candidate);
                        }
                    }

                    positionMap.put(positionName, candidateList);
                }

                positionAdapter.updateData(positionMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VotingPage.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchTotalVotes() {
        votesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    recyclerViewVotes.setVisibility(View.GONE);

                    return;
                }

                votesMap.clear();
                positionList.clear();
                int totalVotes = 0;

                for (DataSnapshot positionSnapshot : snapshot.getChildren()) {
                    String positionName = positionSnapshot.getKey();
                    Map<String, Integer> candidateVotes = new HashMap<>();

                    for (DataSnapshot candidateSnapshot : positionSnapshot.getChildren()) {
                        String candidateName = candidateSnapshot.getKey();
                        Object voteValue = candidateSnapshot.getValue();
                        int voteCount = 0;

                        if (voteValue instanceof Long) {
                            voteCount = ((Long) voteValue).intValue();
                        } else if (voteValue instanceof String) {
                            try {
                                voteCount = Integer.parseInt((String) voteValue);
                            } catch (NumberFormatException e) {
                                Log.e("FirebaseError", "Invalid number format for votes: " + voteValue);
                            }
                        }

                        candidateVotes.put(candidateName, voteCount);
                        totalVotes += voteCount;
                    }

                    votesMap.put(positionName, candidateVotes);
                    positionList.add(positionName);
                }

                // Update UI after fetching votes

                recyclerViewVotes.setVisibility(View.VISIBLE);

                voteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching votes: " + error.getMessage());
            }
        });
    }
}
