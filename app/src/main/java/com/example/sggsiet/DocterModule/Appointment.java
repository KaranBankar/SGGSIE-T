package com.example.sggsiet.DocterModule;

public class Appointment {
    private String studentName;
    private String studentEmail;
    private String date;
    private String time;

    public Appointment() {
        // Default constructor required for Firebase
    }

    public Appointment(String studentName, String studentEmail, String date, String time) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.date = date;
        this.time = time;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
