package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sggsiet.R;
import java.util.List;
import java.util.Map;

public class DisplayVoteAdapter extends RecyclerView.Adapter<DisplayVoteAdapter.ViewHolder> {

    private Context context;
    private List<String> positions;
    private Map<String, Map<String, Integer>> votesMap; // Position -> (Candidate -> Votes)

    public DisplayVoteAdapter(Context context, List<String> positions, Map<String, Map<String, Integer>> votesMap) {
        this.context = context;
        this.positions = positions;
        this.votesMap = votesMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_display_voteresult, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String positionName = positions.get(position);
        holder.textPosition.setText(positionName);

        Map<String, Integer> candidates = votesMap.get(positionName);
        if (candidates != null) {
            StringBuilder voteDetails = new StringBuilder();
            for (Map.Entry<String, Integer> entry : candidates.entrySet()) {
                voteDetails.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
            }
            holder.textCandidatesVotes.setText(voteDetails.toString().trim());
        }
    }

    @Override
    public int getItemCount() {
        return positions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textPosition, textCandidatesVotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textPosition = itemView.findViewById(R.id.textPosition);
            textCandidatesVotes = itemView.findViewById(R.id.textCandidatesVotes);
        }
    }
}
