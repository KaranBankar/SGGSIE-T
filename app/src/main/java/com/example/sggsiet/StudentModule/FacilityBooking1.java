package com.example.sggsiet.StudentModule;

public class FacilityBooking1 {
    private String id;
    private String facilityName;
    private String timeSlot;
    private String status;
    private String studentEmail;
    private String bookingDate;

    // Default constructor (required for Firebase)
    public FacilityBooking1() {
    }

    // Constructor with parameters
    public FacilityBooking1(String id, String facilityName, String timeSlot, String status, String studentEmail, String bookingDate) {
        this.id = id;
        this.facilityName = facilityName;
        this.timeSlot = timeSlot;
        this.status = status;
        this.studentEmail = studentEmail;
        this.bookingDate = bookingDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
}
