package com.example.sggsiet.StudentModule;

public class Booking {
    public String studentName, studentEmail, studentMobile, eventTime, eventDate, eventName;
    public int seatNo;

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
}
