package com.example.sggsiet.DocterModule;

import android.os.Bundle;
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
import java.util.List;

public class ViewStudentReports extends AppCompatActivity {

    private RecyclerView recyclerViewReports;
    private StudentReportAdapter adapter;
    private List<StudentReport> reportList;
    private DatabaseReference complaintsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_reports);

        recyclerViewReports = findViewById(R.id.recyclerViewReports);
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(this));

        reportList = new ArrayList<>();
        adapter = new StudentReportAdapter(reportList);
        recyclerViewReports.setAdapter(adapter);

        complaintsRef = FirebaseDatabase.getInstance().getReference("SGGSIE&T").child("healthreports");

        fetchStudentReports();
    }

    private void fetchStudentReports() {
        complaintsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportList.clear(); // Clear previous data
                for (DataSnapshot data : snapshot.getChildren()) {
                    StudentReport report = data.getValue(StudentReport.class);
                    if (report != null) {
                        reportList.add(report);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter about data update
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewStudentReports.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
