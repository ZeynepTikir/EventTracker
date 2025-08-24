package com.example.eventtracker.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import com.example.eventtracker.data.model.PomodoroEntity;
import com.example.eventtracker.data.model.PomodoroWithTaskName;

import java.util.List;

@Dao
public interface PomodoroDao {

    @Insert
    void insert(PomodoroEntity pomodoro);

    @Update
    void update(PomodoroEntity pomodoro);

    @Delete
    void delete(PomodoroEntity pomodoro);

    @Query("SELECT * FROM pomodoros ORDER BY timestamp DESC")
    List<PomodoroEntity> getAllPomodoros();

    @Query("SELECT * FROM pomodoros WHERE taskId = :taskId ORDER BY timestamp DESC")
    List<PomodoroEntity> getPomodorosForTask(int taskId);

    @Query("SELECT COUNT(*) FROM pomodoros")
    LiveData<Integer> getTotalPomodoros();

    @Query("SELECT * FROM pomodoros ORDER BY timestamp DESC LIMIT 10")
    List<PomodoroEntity> getRecentPomodoros();


    @Query("SELECT COALESCE(SUM(duration), 0) FROM pomodoros")
    LiveData<Long> getTotalFocusMillis();

    @Query("SELECT p.id, p.taskId, t.name AS taskName, p.duration, p.completed, p.timestamp " +
            "FROM pomodoros p INNER JOIN tasks t ON p.taskId = t.id " +
            "ORDER BY p.timestamp DESC")
    List<PomodoroWithTaskName> getRecentPomodorosWithTaskName();
}
