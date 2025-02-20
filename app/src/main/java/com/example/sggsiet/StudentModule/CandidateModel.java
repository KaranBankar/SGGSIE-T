package com.example.sggsiet.StudentModule;


public class CandidateModel {
    private String name, email, enrollmentNo;

    public CandidateModel() { } // Required for Firebase

    public void setName(String name) {
        this.name = name;
    }

    public CandidateModel(String name, String email, String enrollmentNo) {
        this.name = name;
        this.email = email;
        this.enrollmentNo = enrollmentNo;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }

    public String getEnrollmentNo() { return enrollmentNo; }
}
