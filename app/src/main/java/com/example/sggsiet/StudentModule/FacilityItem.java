package com.example.sggsiet.StudentModule;

public class FacilityItem {
    private String name;
    private String timeSlot;
    private int imageResId;
    private String gardName;

    public FacilityItem(String name, String timeSlot, int imageResId, String gardName) {
        this.name = name;
        this.timeSlot = timeSlot;
        this.imageResId = imageResId;
        this.gardName = gardName;
    }

    public String getName() {
        return name;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getGardName() {
        return gardName;
    }
}
