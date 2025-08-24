package com.example.eventtracker.utils;

public class ActivityItem {
    private String title;
    private String time;
    private String description;
    private long timestamp;

    public ActivityItem(String title, String time, String description, long timestamp) {
        this.title = title;
        this.time = time;
        this.description = description;
        this.timestamp = timestamp;
    }

    // Getter metodları
    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
