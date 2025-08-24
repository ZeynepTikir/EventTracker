package com.example.eventtracker.data.model;

public class DayPomodoroStats {
    public int dayIndex;       // 0=Mon, 1=Tue, … 6=Sun
    public long totalDuration; // ms cinsinden

    public DayPomodoroStats(int dayIndex, long totalDuration) {
        this.dayIndex = dayIndex;
        this.totalDuration = totalDuration;
    }
    public long totalMinutes() {
        return totalDuration / 60000;
    }

}
