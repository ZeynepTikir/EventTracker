package com.example.eventtracker.ui.adapter;

import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.data.model.HabitEntity;

public class EventItem {

    public static final int TYPE_TASK = 0;
    public static final int TYPE_HABIT = 1;

    private TaskEntity task;
    private HabitEntity habit;
    private boolean checked;
    private int type;

    // Task constructor
    // Task için constructor
    public EventItem(TaskEntity task, boolean checked) {
        this.task = task;
        this.checked = checked;
        this.type = TYPE_TASK;
    }

    // Habit için constructor
    public EventItem(HabitEntity habit, boolean checked) {
        this.habit = habit;
        this.checked = checked;
        this.type = TYPE_HABIT;
    }

    // Task için checked default olarak false atanır
    public EventItem(TaskEntity task) {
        this.task = task;
        this.checked = false;
        this.type = TYPE_TASK;
    }



    // Getter'lar
    public int getType() {
        return type;
    }

    public TaskEntity getTask() {
        return task;
    }

    public HabitEntity getHabit() {
        return habit;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
