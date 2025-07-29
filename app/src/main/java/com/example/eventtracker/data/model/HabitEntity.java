package com.example.eventtracker.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.eventtracker.data.Converters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "habits")
@TypeConverters(Converters.class)
public class HabitEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String icon;
    private String time;   // Format: "HH:mm"

    private boolean[] days; // Pzt:0, Salı:1, ..., Pazar:6

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean[] getDays() {
        return days;
    }

    public void setDays(boolean[] days) {
        this.days = days;
    }

    public boolean isScheduledForToday() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK); // Pazar = 1, Pazartesi = 2, ..., Cumartesi = 7

        int todayIndex = (dayOfWeek + 5) % 7; // Pazartesi=0, Salı=1, ..., Pazar=6

        return days != null && days.length == 7 && days[todayIndex];
    }

    public boolean isScheduledForDate(String dateStr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = format.parse(dateStr);

            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(date);

            int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK); // Pazar=1
            int index = (dayOfWeek + 5) % 7; // Pazartesi=0

            return days != null && days.length == 7 && days[index];
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
