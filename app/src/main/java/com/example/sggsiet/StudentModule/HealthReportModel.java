package com.example.sggsiet.StudentModule;

public class HealthReportModel {
    private String currentdate;
    private String currenttime;
    private String department;
    private String description;
    private String email;
    private String name;
    private String studentMobile;
    private String enrollmentno;
    private String healthissue;

    // Default constructor required for Firebase
    public HealthReportModel() { }

    public HealthReportModel(String currentdate, String currenttime, String department, String description, String email, String studentMobile, String enrollmentno, String healthissue,String name) {
        this.currentdate = currentdate;
        this.currenttime = currenttime;
        this.department = department;
        this.description = description;
        this.email = email;
        this.studentMobile = studentMobile;
        this.enrollmentno = enrollmentno;
        this.healthissue = healthissue;
       this.name=name;
    }

    public String getCurrentdate() { return currentdate != null ? currentdate : "N/A"; }
    public String getCurrenttime() { return currenttime != null ? currenttime : "N/A"; }
    public String getDepartment() { return department != null ? department : "N/A"; }
    public String getDescription() { return description != null ? description : "N/A"; }
    public String getEmail() { return email != null ? email : "N/A"; }
    public String getName() { return name!= null ? name: "N/A"; }
    public String getStudentMobile() { return studentMobile != null ? studentMobile : "N/A"; }
    public String getEnrollmentno() { return enrollmentno != null ? enrollmentno : "N/A"; }
    public String getHealthissue() { return healthissue != null ? healthissue : "N/A"; }
}
