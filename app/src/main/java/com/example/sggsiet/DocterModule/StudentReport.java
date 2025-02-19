package com.example.sggsiet.DocterModule;

public class StudentReport {
    private String enrollmentno, name, email, department, year, mobile, emergencycontact, position;
    private String currentdate, currenttime, healthissue, description, location, treatment;

    // Required empty constructor for Firebase
    public StudentReport() {}

    public StudentReport(String enrollmentno, String name, String email, String department, String year,
                         String mobile, String emergencycontact, String position, String currentdate,
                         String currenttime, String healthissue, String description, String location, String treatment) {
        this.enrollmentno = enrollmentno;
        this.name = name;
        this.email = email;
        this.department = department;
        this.year = year;
        this.mobile = mobile;
        this.emergencycontact = emergencycontact;
        this.position = position;
        this.currentdate = currentdate;
        this.currenttime = currenttime;
        this.healthissue = healthissue;
        this.description = description;
        this.location = location;
        this.treatment = treatment;
    }

    // Getters for Firebase
    public String getEnrollmentno() { return enrollmentno; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getYear() { return year; }
    public String getMobile() { return mobile; }
    public String getEmergencycontact() { return emergencycontact; }
    public String getPosition() { return position; }
    public String getCurrentdate() { return currentdate; }
    public String getCurrenttime() { return currenttime; }
    public String getHealthissue() { return healthissue; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getTreatment() { return treatment; }
}
