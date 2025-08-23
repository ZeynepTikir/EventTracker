package com.example.eventtracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pomodoros")
public class PomodoroEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int taskId;       // Hangi görev için
    private String taskName;  // Görev adı (gösterim için)
    private long duration;    // Geçen süre (ms)
    private boolean completed;
    private long timestamp;   // Kaydedildiği zaman

    public PomodoroEntity(int taskId, String taskName, long duration, boolean completed, long timestamp) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.duration = duration;
        this.completed = completed;
        this.timestamp = timestamp;
    }

    // Getter & Setter
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
