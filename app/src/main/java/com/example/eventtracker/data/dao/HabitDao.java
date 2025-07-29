package com.example.eventtracker.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.eventtracker.data.model.HabitEntity;
import com.example.eventtracker.data.model.HabitWithCheck;

import java.util.List;

@Dao
public interface HabitDao {

    @Insert
    void insert(HabitEntity habit);

    @Update
    void update(HabitEntity habit);

    @Delete
    void delete(HabitEntity habit);

    @Query("DELETE FROM habits")
    void deleteAllHabits();

    @Query("SELECT * FROM habits ORDER BY time ASC")
    LiveData<List<HabitEntity>> getAllHabits();

    // Yeni: Belirli güne ait alışkanlık ve check bilgileri
    @Query("SELECT habits.*, hc.id AS checkId, hc.date, hc.checked " +
            "FROM habits LEFT JOIN habit_checks hc " +
            "ON habits.id = hc.habitId AND hc.date = :date")
    LiveData<List<HabitWithCheck>> getHabitsWithChecksForDate(String date);


}
