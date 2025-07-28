package com.example.eventtracker.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.eventtracker.data.model.HabitEntity;

import java.util.List;

@Dao
public interface HabitDao {

    @Insert
    void insert(HabitEntity habit);

    @Update
    void update(HabitEntity habit);

    @Delete
    void delete(HabitEntity habit);

    @Query("UPDATE habits SET checked = :checked WHERE id = :id")
    void updateChecked(int id, boolean checked);


    @Query("DELETE FROM habits")
    void deleteAllHabits();

    @Query("SELECT * FROM habits ORDER BY time ASC")
    LiveData<List<HabitEntity>> getAllHabits();
}
