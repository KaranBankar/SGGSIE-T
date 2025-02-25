package com.example.sggsiet.FacultyModule;

public class StudentChat {
    private String name, email, imageUrl;
    private long timestamp;

    public StudentChat() {
    }

    public StudentChat(String name, String email, String imageUrl, long timestamp) {
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getImageUrl() { return imageUrl; }
    public long getTimestamp() { return timestamp; }
}
