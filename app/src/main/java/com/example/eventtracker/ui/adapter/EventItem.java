package com.example.eventtracker.ui.adapter;

import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.data.model.HabitEntity;

public class EventItem {
    public static final int TYPE_TASK = 0;
    public static final int TYPE_HABIT = 1;

    private final int type;
    private final TaskEntity task;
    private final HabitEntity habit;
    private boolean checked;

    public EventItem(TaskEntity task) {
        this.task = task;
        this.habit = null;
        this.type = TYPE_TASK;
    }

    public EventItem(HabitEntity habit) {
        this.habit = habit;
        this.task = null;
        this.type = TYPE_HABIT;
    }

    public int getType() {
        return type;
    }

    public TaskEntity getTask() {
        return task;
    }

    public HabitEntity getHabit() {
        return habit;
    }

    public String getName() {
        if (type == TYPE_TASK) {
            assert task != null;
            return task.getName();
        } else {
            assert habit != null;
            return habit.getName();
        }
    }


    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }
}
