package com.example.sggsiet.StudentModule;

public class Booking {
    public String studentName, studentEmail, studentMobile, eventTime, eventDate, eventName;
    public int seatNo;  // Change from String to int

    public Booking() {}

    public Booking(String studentName, String studentEmail, String studentMobile, String eventTime, String eventDate, String eventName, int seatNo) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentMobile = studentMobile;
        this.eventTime = eventTime;
        this.eventDate = eventDate;
        this.eventName = eventName;
        this.seatNo = seatNo;
    }

    public String getEventName() { return eventName; }
    public String getEventDate() { return eventDate; }
    public String getEventTime() { return eventTime; }
    public int getSeatNo() { return seatNo; }  // Return as int
    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getStudentMobile() { return studentMobile; }
}
