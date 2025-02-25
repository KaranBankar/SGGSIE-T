package com.example.sggsiet.AdminModule;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.AdminModule.Adapter.FundsAdapter;
import com.example.sggsiet.AdminModule.FundsSummary;
import com.example.sggsiet.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetOverview extends AppCompatActivity {

    private PieChart pieChart;
    private BarChart barChart;
    private RecyclerView recyclerView;
    private FundsAdapter adapter;
    private List<FundsSummary> fundsList;
    private DatabaseReference fundsSummaryRef, budgetRequestsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_overview);

        // Initialize UI elements
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        recyclerView = findViewById(R.id.recyclerViewFunds);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fundsList = new ArrayList<>();
        adapter = new FundsAdapter(fundsList);
        recyclerView.setAdapter(adapter);

        // Firebase references
        fundsSummaryRef = FirebaseDatabase.getInstance().getReference("FundsSummary");
        budgetRequestsRef = FirebaseDatabase.getInstance().getReference("BudgetRequests");

        // Load data
        fetchFundsData();
    }

    private void fetchFundsData() {
        fundsSummaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Map<String, Integer> totalFundsMap = new HashMap<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    String requestType = data.getKey();
                    int totalFunds = data.child("totalFunds").getValue(Integer.class);
                    totalFundsMap.put(requestType, totalFunds);
                }

                // Now fetch approved budget requests and update total funds
                fetchApprovedBudgetRequests(totalFundsMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BudgetOverview.this, "Failed to load funds data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchApprovedBudgetRequests(final Map<String, Integer> totalFundsMap) {
        budgetRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String requestType = data.child("requestType").getValue(String.class);
                    String status = data.child("status").getValue(String.class);
                    int requestAmount = Integer.parseInt(data.child("funds").getValue(String.class));

                    if (status != null && status.equals("Approved")) {
                        if (totalFundsMap.containsKey(requestType)) {
                            totalFundsMap.put(requestType, totalFundsMap.get(requestType) + requestAmount);
                        } else {
                            totalFundsMap.put(requestType, requestAmount);
                        }
                    }
                }

                updateCharts(totalFundsMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BudgetOverview.this, "Failed to load budget requests", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCharts(Map<String, Integer> totalFundsMap) {
        fundsList.clear();
        List<PieEntry> pieEntries = new ArrayList<>();
        List<BarEntry> barEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Integer> entry : totalFundsMap.entrySet()) {
            String category = entry.getKey();
            int totalAmount = entry.getValue();

            fundsList.add(new FundsSummary(category, totalAmount));
            pieEntries.add(new PieEntry(totalAmount, category));
            barEntries.add(new BarEntry(index, totalAmount));
            labels.add(category);
            index++;
        }

        // Update UI
        adapter.notifyDataSetChanged();
        updatePieChart(pieEntries);
        updateBarChart(barEntries, labels);
    }

    private void updatePieChart(List<PieEntry> pieEntries) {
        PieDataSet dataSet = new PieDataSet(pieEntries, "Funds Distribution");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(16f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setSliceSpace(3f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setCenterText("Funds Overview");
        pieChart.setCenterTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(14f);

        pieChart.animateY(1000);
        pieChart.invalidate(); // Refresh
    }

    private void updateBarChart(List<BarEntry> barEntries, List<String> labels) {
        BarDataSet dataSet = new BarDataSet(barEntries, "Funds Breakdown");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);

        // Set X-Axis labels
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);

        // Y-Axis settings
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularity(1f);

        barChart.getAxisRight().setEnabled(false);

        // Legend customization
        Legend legend = barChart.getLegend();
        legend.setFormSize(12f);
        legend.setTextSize(10f);
        legend.setTextColor(Color.BLACK);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        // Animate the chart
        barChart.animateY(1000, Easing.EaseInOutQuad);
        barChart.invalidate();
    }
}
