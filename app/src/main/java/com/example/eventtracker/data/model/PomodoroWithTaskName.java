package com.example.eventtracker.data.model;

import androidx.room.ColumnInfo;

public class PomodoroWithTaskName {

    public int id;
    public int taskId;

    @ColumnInfo(name = "taskName")
    public String taskName;

    public long duration;
    public boolean completed;
    public long timestamp;

    // Boş constructor
    public PomodoroWithTaskName() {}

    // İstersen getter/setter ekleyebilirsin
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
