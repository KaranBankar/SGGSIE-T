package com.example.sggsiet.StudentModule;

public class CheatRecord {
    private String studentName, enrollmentNo, subjectName, date, imageUrl;

    public CheatRecord() { } // Required for Firebase

    public CheatRecord(String studentName, String enrollmentNo, String subjectName, String date, String imageUrl) {
        this.studentName = studentName;
        this.enrollmentNo = enrollmentNo;
        this.subjectName = subjectName;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public String getStudentName() { return studentName; }
    public String getEnrollmentNo() { return enrollmentNo; }
    public String getSubjectName() { return subjectName; }
    public String getDate() { return date; }
    public String getImageUrl() { return imageUrl; }
}
