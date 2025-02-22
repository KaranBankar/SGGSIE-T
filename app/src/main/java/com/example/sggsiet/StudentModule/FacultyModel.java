package com.example.sggsiet.StudentModule;


public class FacultyModel {
    private String name, email, mobile, department, position;

    public FacultyModel(String name, String email, String mobile, String department, String position) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.department = department;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getDepartment() {
        return department;
    }

    public String getPosition() {
        return position;
    }
}
