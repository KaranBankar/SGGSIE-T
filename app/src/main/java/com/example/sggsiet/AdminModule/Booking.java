package com.example.sggsiet.AdminModule;

// Booking.java
public class Booking {
    private String bookingDate;
    private String facilityName;
    private String status;
    private String studentEmail;
    private String timeSlot;

    // Default constructor required for calls to DataSnapshot.getValue(Booking.class)
    public Booking() {
    }

    public Booking(String bookingDate, String facilityName, String status, String studentEmail, String timeSlot) {
        this.bookingDate = bookingDate;
        this.facilityName = facilityName;
        this.status = status;
        this.studentEmail = studentEmail;
        this.timeSlot = timeSlot;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getStatus() {
        return status;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getTimeSlot() {
        return timeSlot;
    }
}