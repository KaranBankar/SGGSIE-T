package com.example.sggsiet.AdminModule;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.example.sggsiet.StudentModule.AdminComplaintShowAdapter;
import com.example.sggsiet.StudentModule.Complaint;
import com.example.sggsiet.StudentModule.ComplaintAdapter;
import com.example.sggsiet.SubmitComplaint;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PrincipalDashboard extends AppCompatActivity {
    private CardView addalldata,election,leavemodule,facility,budget,complaints,admissions,exam,notification;
    private BarChart budgetChart;
    private RecyclerView recyclerViewComplaints; // RecyclerView for complaints
    private AdminComplaintShowAdapter complaintAdapter; // Adapter for complaints
    private List<Complaint> complaintList;
    MaterialButton viewall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal_dashboard);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




       initilizeallviews();
       setlisteners();
        fetchBudgetData();
        setupRecyclerView(); // Set up the RecyclerView




    }

    private void setupRecyclerView() {
        complaintList = new ArrayList<>();
        complaintAdapter = new AdminComplaintShowAdapter(this, complaintList);
        recyclerViewComplaints.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComplaints.setAdapter(complaintAdapter);
    }

    private void fetchComplaints() {
        DatabaseReference complaintsRef = FirebaseDatabase.getInstance().getReference("complaints"); // Reference to your complaints data
        complaintsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                complaintList.clear(); // Clear the list before adding new data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Complaint complaint = dataSnapshot.getValue(Complaint.class); // Assuming you have a Complaint class
                    complaintList.add(complaint); // Add complaint to the list
                }
                complaintAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(PrincipalDashboard.this, "Failed to load complaints", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchBudgetData() {
        DatabaseReference budgetRef = FirebaseDatabase.getInstance().getReference("FundsSummary"); // Reference to your Firebase data
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<BarEntry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                int index = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    String category = data.getKey();
                    float amount = data.child("totalFunds").getValue(Float.class); // Adjust according to your data structure
                    entries.add(new BarEntry(index++, amount));
                    labels.add(category); // Add category to labels
                }

                // Create and configure the BarDataSet
                BarDataSet dataSet = new BarDataSet(entries, "Funds Breakdown");
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

                // Create BarData and set it to the chart
                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.5f);
                budgetChart.setData(barData);
                budgetChart.getDescription().setEnabled(false); // Disable description

                // Set X-Axis labels
                XAxis xAxis = budgetChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); // Set labels
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1); // Only show integer values
                xAxis.setTextSize(8f);
                xAxis.setTextColor(Color.BLACK);


                // Y-Axis settings
                YAxis leftAxis = budgetChart.getAxisLeft();
                leftAxis.setTextSize(12f);
                leftAxis.setTextColor(Color.BLACK);
                leftAxis.setDrawGridLines(true); // Show grid lines for better readability
                leftAxis.setGranularity(1f); // Only show integer values

                budgetChart.getAxisRight().setEnabled(false); // Disable right Y-Axis

                // Legend customization
                Legend legend = budgetChart.getLegend();
                legend.setFormSize(12f);
                legend.setTextSize(8f);
                legend.setTextColor(Color.RED);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

                // Animate the chart
                budgetChart.animateY(1000, Easing.EaseInOutQuad); // Add easing for a smoother animation
                budgetChart.invalidate(); // Refresh the chart

                // Set value selection listener
                budgetChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
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

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(PrincipalDashboard.this, "Failed to load budget data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setlisteners() {
        addalldata.setOnClickListener(view -> {
            startActivity(new Intent(this,UploadData.class));
        });
        election.setOnClickListener(view -> {
            startActivity(new Intent(this,ElectionModule.class));
        });
        facility.setOnClickListener(view -> {
            startActivity(new Intent(this, FacilityRegistration.class));
        });

        leavemodule.setOnClickListener(view -> {
            startActivity(new Intent(this,LeavesManagement.class));
        });
        budget.setOnClickListener(v->{
            startActivity(new Intent(this,BudgetOverview.class));
        });
        complaints.setOnClickListener(v->{
            startActivity(new Intent(this,HomeComplaints.class));
        });
        admissions.setOnClickListener(v->{
            startActivity(new Intent(this,AdmissionModule.class));
        });
        exam.setOnClickListener(v-> {
            startActivity(new Intent(this, ExamModule.class));
        });

        notification.setOnClickListener(v->{
            startActivity(new Intent(this,NotificationModule.class));
        });


        final boolean[] isComplaintsVisible = {false};
        viewall.setOnClickListener(v -> {
            if (!isComplaintsVisible[0]) {
                // Show complaints
                fetchComplaints();
                recyclerViewComplaints.setVisibility(View.VISIBLE);
                viewall.setText("Hide Complaints");
            } else {
                // Hide complaints
                recyclerViewComplaints.setVisibility(View.GONE);
                viewall.setText("View All");
            }
            isComplaintsVisible[0] = !isComplaintsVisible[0]; // Toggle state
        });
    }

    private void initilizeallviews() {
        addalldata=findViewById(R.id.addalldata);
        election=findViewById(R.id.electionscreen);
        complaints=findViewById(R.id.complainmodule);
        budget=findViewById(R.id.budgetmodule);
        leavemodule=findViewById(R.id.leavemodule);
        facility=findViewById(R.id.facilitymodule);
        budgetChart = findViewById(R.id.budgetChart);
        recyclerViewComplaints = findViewById(R.id.complaintsRecyclerView); // Initialize RecyclerView
        viewall=findViewById(R.id.viewallcomp);
        admissions=findViewById(R.id.admissionModule);
        exam=findViewById(R.id.notification);
        notification=findViewById(R.id.notification);

    }
}