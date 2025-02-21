package com.example.sggsiet.AdminModule;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.example.sggsiet.StudentModule.DisplayVoteAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ElectionModule extends AppCompatActivity {

    private BarChart barChart;
    private RecyclerView recyclerViewCandidates;
    private DisplayVoteAdapter adapter;
    private List<String> positions = new ArrayList<>();
    private Map<String, Map<String, Integer>> votesMap = new HashMap<>();
    private DatabaseReference databaseReference;
    private final int[] COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.YELLOW}; // Predefined colors

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_module);

        barChart = findViewById(R.id.barChart);
        recyclerViewCandidates = findViewById(R.id.recyclerViewCandidates);

        recyclerViewCandidates.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DisplayVoteAdapter(this, positions, votesMap);
        recyclerViewCandidates.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Votes");

        loadLiveVotes();
    }

    private void loadLiveVotes() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                positions.clear();
                votesMap.clear();
                List<BarEntry> entries = new ArrayList<>();
                List<String> candidateNames = new ArrayList<>();
                int index = 0;

                for (DataSnapshot positionSnapshot : snapshot.getChildren()) {
                    String positionName = positionSnapshot.getKey();
                    Map<String, Integer> candidateVotes = new HashMap<>();

                    for (DataSnapshot candidateSnapshot : positionSnapshot.getChildren()) {
                        String candidateName = candidateSnapshot.getKey();
                        int voteCount = candidateSnapshot.getValue(Integer.class);

                        candidateVotes.put(candidateName, voteCount);
                        entries.add(new BarEntry(index, voteCount));
                        candidateNames.add(candidateName);
                        index++;
                    }

                    positions.add(positionName);
                    votesMap.put(positionName, candidateVotes);
                }

                adapter.notifyDataSetChanged();
                updateChart(entries, candidateNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ElectionModule.this, "Failed to load votes", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
    }

    private void updateChart(List<BarEntry> entries, List<String> labels) {
        BarDataSet dataSet = new BarDataSet(entries, "Live Vote Count");
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        // Assigning different colors for each bar dynamically
        List<Integer> colors = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < labels.size(); i++) {
            colors.add(COLORS[random.nextInt(COLORS.length)]);
        }
        dataSet.setColors(colors);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f); // Adjust bar width for better spacing
        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(12f);
        barChart.getAxisRight().setEnabled(false);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setForm(Legend.LegendForm.SQUARE);
        barChart.getLegend().setTextSize(12f);
        barChart.getLegend().setTextColor(Color.BLACK);

        barChart.invalidate();
    }
}
