package com.example.sggsiet.StudentModule;

public class BudgetRequest {
    private String requestId;
    private String enrollmentNo;
    private String studentName;
    private String requestType;
    private String title;

    public String getFunds() {
        return Funds;
    }

    public void setFunds(String funds) {
        Funds = funds;
    }

    private String Funds;
    private String description;
    private String attachmentUrl;
    private String status; // Status added for tracking
    private long timestamp;

    // Empty constructor required for Firebase
    public BudgetRequest() {}

    // Constructor with all parameters
    public BudgetRequest(String requestId, String enrollmentNo, String studentName, String requestType, String title, String description,String Funds, String attachmentUrl, String status, long timestamp) {
        this.requestId = requestId;
        this.enrollmentNo = enrollmentNo;
        this.studentName = studentName;
        this.requestType = requestType;
        this.title = title;
        this.Funds=Funds;
        this.description = description;
        this.attachmentUrl = attachmentUrl;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getEnrollmentNo() { return enrollmentNo; }
    public void setEnrollmentNo(String enrollmentNo) { this.enrollmentNo = enrollmentNo; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
