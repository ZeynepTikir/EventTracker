package com.example.eventtracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habit_checks")
public class HabitCheckEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int habitId;

    private String date; // yyyy-MM-dd

    private boolean checked;

    // Constructor
    public HabitCheckEntity(int habitId, String date, boolean checked) {
        this.habitId = habitId;
        this.date = date;
        this.checked = checked;
    }

    public HabitCheckEntity() {
        // Boş constructor
    }


    // Getter / Setter
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getHabitId() { return habitId; }

    public void setHabitId(int habitId) { this.habitId = habitId; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public boolean isChecked() { return checked; }

    public void setChecked(boolean checked) { this.checked = checked; }
}
