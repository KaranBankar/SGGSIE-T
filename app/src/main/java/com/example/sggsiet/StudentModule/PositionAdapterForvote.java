package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PositionAdapterForvote extends RecyclerView.Adapter<PositionAdapterForvote.PositionViewHolder> {
    private final Context context;
    private Map<String, List<CandidateModel>> positionMap;
    private final List<String> positionNames;

    public PositionAdapterForvote(Context context, Map<String, List<CandidateModel>> positionMap) {
        this.context = context;
        this.positionMap = positionMap;
        this.positionNames = new ArrayList<>(positionMap.keySet());
    }

    // ✅ Method to update data dynamically
    public void updateData(Map<String, List<CandidateModel>> newPositionMap) {
        this.positionMap = newPositionMap;
        this.positionNames.clear();
        this.positionNames.addAll(newPositionMap.keySet());
        notifyDataSetChanged();  // Notify adapter to refresh RecyclerView
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

        StringBuilder candidatesText = new StringBuilder();
        if (candidates != null && !candidates.isEmpty()) {
            for (CandidateModel candidate : candidates) {
                candidatesText.append("• ").append(candidate.getName()).append("\n");
            }
        } else {
            candidatesText.append("No candidates available.");
        }

        holder.candidatesList.setText(candidatesText.toString().trim());
    }

    @Override
    public int getItemCount() {
        return positionNames.size();
    }

    static class PositionViewHolder extends RecyclerView.ViewHolder {
        TextView positionTitle, candidatesList;

        PositionViewHolder(View itemView) {
            super(itemView);
            positionTitle = itemView.findViewById(R.id.textPositionTitle);
            candidatesList = itemView.findViewById(R.id.textCandidatesList);
        }
    }
}
