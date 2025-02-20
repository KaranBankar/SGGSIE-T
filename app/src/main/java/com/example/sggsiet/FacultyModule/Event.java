package com.example.sggsiet.FacultyModule;
public class Event {
    private String eventId, name, description, date, time, location, imageUrl, status;

    public Event() { } // Required for Firebase

    public Event(String eventId, String name, String description, String date, String time,
                 String location, String imageUrl, String status) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public String getEventId() { return eventId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
    public String getImageUrl() { return imageUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
