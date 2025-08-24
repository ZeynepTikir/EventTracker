package com.example.eventtracker.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class TaskEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String icon;
    private String time;  // Format: "HH:mm"
    private String date;  // Format: "yyyy-MM-dd"
    @ColumnInfo(name = "checked")
    private boolean checked;
    @ColumnInfo(name = "completedTimestamp")
    private long completedTimestamp; // 0 = tamamlanmadı, diğer sayılar tamamlandı ve zaman milis cinsinden

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getCompletedTimestamp() { return completedTimestamp; }
    public void setCompletedTimestamp(long completedTimestamp) { this.completedTimestamp = completedTimestamp; }
}
