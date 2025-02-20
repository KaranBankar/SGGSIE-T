package com.example.sggsiet.StudentModule;

public class Event {
    public String eventId, name, description, date, time, location, imageUrl, seats, bookings;

    public Event() {
        // Default constructor required for Firebase
    }

    public Event(String eventId, String name, String description, String date, String time, String location, String imageUrl, String seats) {

    }

    public Event(String eventId, String name, String desc, String date, String time, String location, String imageUrl, String pending, String seats, String bookings) {
        this.eventId = eventId;
        this.name = name;
        this.description = desc;
        this.date = date;
        this.time = time;
        this.location = location;
        this.imageUrl = imageUrl;
        this.seats = seats;
        this.bookings = bookings; // Default value for bookings
    }
}
