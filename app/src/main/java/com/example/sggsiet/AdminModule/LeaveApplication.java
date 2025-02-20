package com.example.sggsiet.AdminModule;

public class LeaveApplication {
    private String name;
    private String email;
    private String reason;
    private String status;
    private String applicationDate;
    private String mobile;
    private String submissionDate;

    public LeaveApplication() {
        this.status = "Pending";
    }

    public LeaveApplication(String name, String email, String reason, String applicationDate, String mobile, String submissionDate) {
        this.name = name;
        this.email = email;
        this.reason = reason;
        this.status = "Pending";
        this.applicationDate = applicationDate;
        this.mobile = mobile;
        this.submissionDate = submissionDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    @Override
    public String toString() {
        return "LeaveApplication{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", applicationDate='" + applicationDate + '\'' +
                ", mobile='" + mobile + '\'' +
                ", submissionDate='" + submissionDate + '\'' +
                '}';
    }
}