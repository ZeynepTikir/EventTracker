package com.example.eventtracker.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.eventtracker.data.model.TaskEntity;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    long insert(TaskEntity task);

    @Update
    void update(TaskEntity task);

    @Delete
    void delete(TaskEntity task);

    @Query("DELETE FROM tasks")
    void deleteAllTasks();

    @Query("SELECT * FROM tasks ORDER BY time ASC")
    LiveData<List<TaskEntity>> getAllTasks();

    @Query("SELECT * FROM tasks ORDER BY time ASC")
    List<TaskEntity> getAllTasksSync();  // LiveData değil, direkt liste döner


    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY time ASC")
    LiveData<List<TaskEntity>> getTasksByDate(String date);


    @Query("UPDATE tasks SET checked = :checked WHERE id = :id")
    void updateChecked(int id, boolean checked);


}
