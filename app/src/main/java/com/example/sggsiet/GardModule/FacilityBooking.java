package com.example.sggsiet.GardModule;

public class FacilityBooking {
    private String id;
    private String bookingDate;
    private String facilityName;
    private String status;
    private String studentEmail;
    private String timeSlot;
    private String reason;  // ✅ Add this field for rejection reason

    public FacilityBooking() {
        // Default constructor (needed for Firebase)
    }

    public FacilityBooking(String id, String bookingDate, String facilityName, String status, String studentEmail, String timeSlot, String reason) {
        this.id = id;
        this.bookingDate = bookingDate;
        this.facilityName = facilityName;
        this.status = status;
        this.studentEmail = studentEmail;
        this.timeSlot = timeSlot;
        this.reason = reason; // ✅ Initialize reason
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getReason() { return reason; }  // ✅ Getter for reason
    public void setReason(String reason) { this.reason = reason; } // ✅ Setter for reason
}
