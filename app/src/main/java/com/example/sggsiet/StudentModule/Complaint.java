package com.example.sggsiet.StudentModule;

public class Complaint {
    private String title;
    private String description;
    private String complaintType;
    private String imageUrl;

    // Empty constructor for Firebase
    public Complaint() {}

    public Complaint(String title, String description, String complaintType, String imageUrl) {
        this.title = title;
        this.description = description;
        this.complaintType = complaintType;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
