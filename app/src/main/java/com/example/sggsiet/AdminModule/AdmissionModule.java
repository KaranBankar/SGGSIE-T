package com.example.sggsiet.AdminModule;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdmissionModule extends AppCompatActivity {

    private TextView totalStudents, totalMales, totalFemales;
    private Spinner departmentSpinner;
    private PieChart pieChartGender;
    private BarChart barChartDepartment;
    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private LineChart lineChartYear;
    private MaterialButton filterButton,viewallstudnts;

    private List<Student> studentList;
    private List<Student> filteredList; // List to hold filtered students

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admission_module);

        // Initialize UI components
        totalStudents = findViewById(R.id.totalStudents);
        totalMales = findViewById(R.id.totalMales);
        totalFemales = findViewById(R.id.totalFemales);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        pieChartGender = findViewById(R.id.pieChartGender);
        barChartDepartment = findViewById(R.id.barChartDepartment);
        lineChartYear = findViewById(R.id.lineChartYear);
        filterButton = findViewById(R.id.filterButton);
        recyclerView = findViewById(R.id.allstudenstss);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set layout manager

        studentList = new ArrayList<>();
        filteredList = new ArrayList<>();
        fetchStudentData();

        // Set up filter button click listener
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByDepartment();
            }
        });
        viewallstudnts = findViewById(R.id.viewallstudents);
        viewallstudnts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllStudents();
            }
        });
    }

    private void showAllStudents() {
        if (studentList.isEmpty()) {
            Toast.makeText(this, "No students available", Toast.LENGTH_SHORT).show();
            return;
        }
        studentAdapter = new StudentAdapter(studentList);
        recyclerView.setAdapter(studentAdapter);
        recyclerView.setVisibility(View.VISIBLE); // Make RecyclerView visible
    }

    private void fetchStudentData() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("dummy_student_data.csv");

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            Log.d("CSV_FETCH", "CSV file fetched successfully. Size: " + bytes.length + " bytes");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.ByteArrayInputStream(bytes)))) {
                String line;

                // Skip the header line
                reader.readLine();

                while ((line = reader.readLine()) != null) {
                    Log.d("CSV_LINE", "Processing line: " + line);

                    String[] columns = line.split(",");

                    if (columns.length < 9) continue; // Ensure at least 9 columns exist

                    String enrollmentNo = columns[0].trim();
                    String studentName = columns[1].trim();
                    String email = columns[2].trim();
                    String department = columns[3].trim();
                    String year = columns[4].trim();
                    String mobile = columns[5].trim();
                    String emergencyContact = columns[6].trim();
                    String position = columns[7].trim();
                    String gender = columns[8].trim(); // Assuming gender is the 9th column

                    // Create a new Student object and add it to the list
                    Student student = new Student(enrollmentNo, studentName, email, department, year, mobile, emergencyContact, position, gender);
                    studentList.add(student);
                }

                Log.d("CSV_SUCCESS", "Total students fetched: " + studentList.size());
                updateUI(studentList); // Pass the full list initially

            } catch (Exception e) {
                Log.e("CSV_ERROR", "Error reading student data", e);
                Toast.makeText(this, "Error reading student data", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("CSV_ERROR", "Failed to fetch student data", e);
            Toast.makeText(this, "Failed to fetch student data", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI(List<Student> students) {
        // Update total counts
        totalStudents.setText("Total Students: " + students.size());
        int maleCount = 0;
        int femaleCount = 0;

        for (Student student : students) {
            if ("Male".equalsIgnoreCase(student.getGender())) {
                maleCount++;
            } else if ("Female".equalsIgnoreCase(student.getGender())) {
                femaleCount++;
            }
        }

        totalMales.setText("Total Males: " + maleCount);
        totalFemales.setText("Total Females: " + femaleCount);

        // Populate department spinner
        HashMap<String, Integer> departmentCount = new HashMap<>();
        for (Student student : students) {
            departmentCount.put(student.getDepartment(), departmentCount.getOrDefault(student.getDepartment(), 0) + 1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(departmentCount.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(adapter);

        // Update PieChart for Gender Distribution
        List<PieEntry> genderEntries = new ArrayList<>();
        if (maleCount > 0) genderEntries.add(new PieEntry(maleCount, "Males"));
        if (femaleCount > 0) genderEntries.add(new PieEntry(femaleCount, "Females"));

        PieDataSet genderDataSet = new PieDataSet(genderEntries, "Gender Distribution");
        genderDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        genderDataSet.setValueTextSize(12f);
        PieData genderData = new PieData(genderDataSet);
        pieChartGender.setData(genderData);
        pieChartGender.getDescription().setEnabled(false);
        pieChartGender.setUsePercentValues(true);
        pieChartGender.animateY(1000);
        pieChartGender.invalidate(); // Refresh the chart

        // Update BarChart for Department Distribution
        List<BarEntry> departmentEntries = new ArrayList<>();
        List<String> departmentLabels = new ArrayList<>();
        int index = 0;
        for (String department : departmentCount.keySet()) {
            departmentEntries.add(new BarEntry(index, departmentCount.get(department)));
            departmentLabels.add(department);
            index++;
        }

        BarDataSet departmentDataSet = new BarDataSet(departmentEntries, "Department Distribution");
        departmentDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        departmentDataSet.setValueTextSize(12f);
        BarData departmentData = new BarData(departmentDataSet);
        barChartDepartment.setData(departmentData);
        barChartDepartment.getXAxis().setValueFormatter(new IndexAxisValueFormatter(departmentLabels));
        barChartDepartment.getXAxis().setGranularity(1f);
        barChartDepartment.getDescription().setEnabled(false);
        barChartDepartment.animateY(1000);
        barChartDepartment.invalidate(); // Refresh the chart

        // Update LineChart for Year Distribution
        HashMap<String, Integer> yearCount = new HashMap<>();
        for (Student student : students) {
            yearCount.put(student.getYear(), yearCount.getOrDefault(student.getYear(), 0) + 1);
        }

        List<Entry> yearEntries = new ArrayList<>();
        List<String> yearLabels = new ArrayList<>();
        int yearIndex = 0;
        for (String year : yearCount.keySet()) {
            yearEntries.add(new Entry(yearIndex, yearCount.get(year)));
            yearLabels.add(year);
            yearIndex++;
        }

        LineDataSet yearDataSet = new LineDataSet(yearEntries, "Year Distribution");
        yearDataSet.setColor(ColorTemplate.MATERIAL_COLORS[0]);
        yearDataSet.setValueTextSize(12f);
        LineData yearData = new LineData(yearDataSet);
        lineChartYear.setData(yearData);
        lineChartYear.getXAxis().setValueFormatter(new IndexAxisValueFormatter(yearLabels));
        lineChartYear.getXAxis().setGranularity(1f);
        lineChartYear.getDescription().setEnabled(false);
        lineChartYear.animateX(1000);
        lineChartYear.invalidate(); // Refresh the chart
    }


    private void filterByDepartment() {
        String selectedDepartment = departmentSpinner.getSelectedItem().toString();
        filteredList.clear();

        for (Student student : studentList) {
            if (student.getDepartment().equalsIgnoreCase(selectedDepartment)) {
                filteredList.add(student);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No students found in this department", Toast.LENGTH_SHORT).show();
        } else {
            updateUI(filteredList); // Do NOT update the spinner again
        }
    }



    public class Student {
        private String enrollmentNo;
        private String name;
        private String email;
        private String department;
        private String year;
        private String mobile;
        private String emergencyContact;
        private String position;
        private String gender;

        // Constructor
        public Student(String enrollmentNo, String name, String email, String department, String year,
                       String mobile, String emergencyContact, String position, String gender) {
            this.enrollmentNo = enrollmentNo;
            this.name = name;
            this.email = email;
            this.department = department;
            this.year = year;
            this.mobile = mobile;
            this.emergencyContact = emergencyContact;
            this.position = position;
            this.gender = gender;
        }

        // Getters
        public String getEnrollmentNo() { return enrollmentNo; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getDepartment() { return department; }
        public String getYear() { return year; }
        public String getMobile() { return mobile; }
        public String getEmergencyContact() { return emergencyContact; }
        public String getPosition() { return position; }
        public String getGender() { return gender; }
    }
}