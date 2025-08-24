package com.example.eventtracker.data.model;

public class TaskDayStats {
    public int dayIndex;       // 0=Mon ... 6=Sun
    public float totalTasks;
    public float completedTasks;

    public TaskDayStats(int dayIndex, float totalTasks, float completedTasks) {
        this.dayIndex = dayIndex;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
    }
}
