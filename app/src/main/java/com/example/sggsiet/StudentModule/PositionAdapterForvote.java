package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PositionAdapterForvote extends RecyclerView.Adapter<PositionAdapterForvote.PositionViewHolder> {
    private final Context context;
    private Map<String, List<CandidateModel>> positionMap;
    private final List<String> positionNames;
    private final Map<String, String> userVotes; // Store user votes for each position

    public PositionAdapterForvote(Context context, Map<String, List<CandidateModel>> positionMap) {
        this.context = context;
        this.positionMap = positionMap;
        this.positionNames = new ArrayList<>(positionMap.keySet());
        this.userVotes = new java.util.HashMap<>();
    }

    public void updateData(Map<String, List<CandidateModel>> newPositionMap) {
        this.positionMap = newPositionMap;
        this.positionNames.clear();
        this.positionNames.addAll(newPositionMap.keySet());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_positionforvote, parent, false);
        return new PositionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PositionViewHolder holder, int position) {
        String positionName = positionNames.get(position);
        List<CandidateModel> candidates = positionMap.get(positionName);

        holder.positionTitle.setText(positionName);
        holder.radioGroupCandidates.removeAllViews();

        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean hasVoted = sharedPreferences.getBoolean("hasVoted_" + positionName, false);

        if (candidates != null && !candidates.isEmpty()) {
            for (CandidateModel candidate : candidates) {
                RadioButton radioButton = new RadioButton(context);
                radioButton.setText(candidate.getName());
                radioButton.setTag(candidate.getName());
                holder.radioGroupCandidates.addView(radioButton);
            }
        } else {
            TextView noCandidatesText = new TextView(context);
            noCandidatesText.setText("No candidates available.");
            holder.radioGroupCandidates.addView(noCandidatesText);
        }

        // 🔹 If user has already voted, disable UI
        if (hasVoted) {
            holder.voteButton.setEnabled(false);
            holder.radioGroupCandidates.setEnabled(false);
        } else {
            holder.voteButton.setEnabled(true);
            holder.radioGroupCandidates.setEnabled(true);
        }

        // Handle Vote Button Click
        holder.voteButton.setOnClickListener(v -> {
            int selectedId = holder.radioGroupCandidates.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(context, "Please select a candidate to vote", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadioButton = holder.itemView.findViewById(selectedId);
            String selectedCandidate = selectedRadioButton.getTag().toString();

            saveVoteToFirebase(positionName, selectedCandidate);
        });
    }


    @Override
    public int getItemCount() {
        return positionNames.size();
    }

    static class PositionViewHolder extends RecyclerView.ViewHolder {
        TextView positionTitle;
        RadioGroup radioGroupCandidates;
        View voteButton;

        PositionViewHolder(View itemView) {
            super(itemView);
            positionTitle = itemView.findViewById(R.id.textPositionTitle);
            radioGroupCandidates = itemView.findViewById(R.id.radioGroupCandidates);
            voteButton = itemView.findViewById(R.id.buttonVote);
        }
    }

    // ✅ Save Vote to Firebase
    // ✅ Save Vote to Firebase (Increment Count)
    private void saveVoteToFirebase(String position, String candidate) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("enrollmentNo", ""); // Fetch user ID from SharedPreferences
        String voteKey = "hasVoted_" + position; // Unique key for each position

        // 🔹 Check if user has already voted (Using SharedPreferences)
        boolean hasVoted = sharedPreferences.getBoolean(voteKey, false);
        if (hasVoted) {
            Toast.makeText(context, "You have already voted for " + position, Toast.LENGTH_SHORT).show();
            return; // Stop further execution
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Votes").child(position).child(candidate);

        dbRef.runTransaction(new com.google.firebase.database.Transaction.Handler() {
            @Override
            public com.google.firebase.database.Transaction.Result doTransaction(@NonNull com.google.firebase.database.MutableData currentData) {
                Object currentVotesObj = currentData.getValue();
                long currentVotes = 0; // Default value

                if (currentVotesObj instanceof Long) {
                    currentVotes = (Long) currentVotesObj;
                } else if (currentVotesObj instanceof String) {
                    try {
                        currentVotes = Long.parseLong((String) currentVotesObj);
                    } catch (NumberFormatException e) {
                        currentVotes = 0;
                    }
                }

                currentData.setValue(currentVotes + 1); // Increment vote count
                return com.google.firebase.database.Transaction.success(currentData);
            }

            @Override
            public void onComplete(@androidx.annotation.Nullable DatabaseError error, boolean committed, @androidx.annotation.Nullable DataSnapshot snapshot) {
                if (error != null) {
                    Log.e("FirebaseVote", "Error updating vote count: " + error.getMessage());
                    Toast.makeText(context, "Failed to submit vote", Toast.LENGTH_SHORT).show();
                } else {
                    // ✅ Vote submitted successfully, store in SharedPreferences to prevent re-voting
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(voteKey, true); // Mark this position as voted
                    editor.apply();

                    Toast.makeText(context, "Vote submitted successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("FirebaseVote", "Vote count updated successfully");

                    // ✅ Disable voting UI for this position
                    notifyDataSetChanged();
                }
            }
        });
    }



}
