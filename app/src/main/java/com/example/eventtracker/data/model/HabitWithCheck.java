package com.example.eventtracker.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class HabitWithCheck {
    @Embedded
    private HabitEntity habit;

    @Relation(parentColumn = "id", entityColumn = "habitId", entity = HabitCheckEntity.class)
    private HabitCheckEntity check;

    public HabitWithCheck(HabitEntity habit, HabitCheckEntity check) {
        this.habit = habit;
        this.check = check;
    }

    public HabitEntity getHabit() {
        return habit;
    }

    public HabitCheckEntity getCheck() {
        return check;
    }
}
