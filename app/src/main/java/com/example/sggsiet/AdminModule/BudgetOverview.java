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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BudgetOverview extends AppCompatActivity {

    private PieChart pieChart;
    private BarChart barChart;
    private RecyclerView recyclerView;
    private FundsAdapter adapter;
    private List<FundsSummary> fundsList;
    private DatabaseReference fundsSummaryRef;

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

        // Firebase reference
        fundsSummaryRef = FirebaseDatabase.getInstance().getReference("FundsSummary");

        // Load data
        fetchFundsData();
    }

    private void fetchFundsData() {
        fundsSummaryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fundsList.clear();
                List<PieEntry> pieEntries = new ArrayList<>();
                List<BarEntry> barEntries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                int index = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    String requestType = data.getKey();
                    int totalFunds = data.child("totalFunds").getValue(Integer.class);

                    fundsList.add(new FundsSummary(requestType, totalFunds));

                    // Add data to PieChart & BarChart
                    pieEntries.add(new PieEntry(totalFunds, requestType));
                    barEntries.add(new BarEntry(index, totalFunds));
                    labels.add(requestType);
                    index++;
                }

                // Update UI
                adapter.notifyDataSetChanged();
                updatePieChart(pieEntries);
                updateBarChart(barEntries, labels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BudgetOverview.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePieChart(List<PieEntry> pieEntries) {
        PieDataSet dataSet = new PieDataSet(pieEntries, "Funds Distribution");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(16f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setSliceSpace(3f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setCenterText("Funds Overview");
        pieChart.setCenterTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(14f);

        // Improved legend


        // Set a value formatter to avoid overlapping labels
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setExtraOffsets(5, 5, 5, 5); // Add extra offsets to avoid clipping
        pieChart.setDrawEntryLabels(true); // Enable entry labels
        pieChart.setEntryLabelTextSize(12f); // Set entry label text size
        pieChart.setEntryLabelColor(Color.BLACK); // Set entry label color

        pieChart.animateY(1000);
        pieChart.invalidate(); // Refresh
    }

    private void updateBarChart(List<BarEntry> barEntries, List<String> labels) {
        BarDataSet dataSet = new BarDataSet(barEntries, "Funds Breakdown");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE); // Change text color for better visibility
        dataSet.setDrawValues(true); // Show values on top of bars
        dataSet.setValueTextSize(12f);

        // Add value labels on top of the bars
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return "₹" + (int) barEntry.getY(); // Format the value as currency
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);

        // Set X-Axis labels
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); // Set labels
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Only show integer values
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);

        // Y-Axis settings
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true); // Show grid lines for better readability
        leftAxis.setGranularity(1f); // Only show integer values

        barChart.getAxisRight().setEnabled(false); // Disable right Y-Axis

        // Legend customization
        Legend legend = barChart.getLegend();
        legend.setFormSize(12f);
        legend.setTextSize(8f);
        legend.setTextColor(Color.BLACK);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        // Animate the chart
        barChart.animateY(1000, Easing.EaseInOutQuad); // Add easing for a smoother animation
        barChart.invalidate(); // Refresh


        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // Show a Toast or a custom tooltip with the value
                String value = "₹" + (int) e.getY();
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
                // Handle when nothing is selected
            }
        });

    }




}
