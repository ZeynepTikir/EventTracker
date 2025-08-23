package com.example.eventtracker.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import com.example.eventtracker.data.model.PomodoroEntity;

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
}
