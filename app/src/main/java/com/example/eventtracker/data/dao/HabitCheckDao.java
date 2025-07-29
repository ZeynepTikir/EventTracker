package com.example.eventtracker.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.eventtracker.data.model.HabitCheckEntity;

import java.util.List;

@Dao
public interface HabitCheckDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HabitCheckEntity habitCheck); // insertOrUpdate yerine insert kullan

    @Query("SELECT * FROM habit_checks WHERE habitId = :habitId AND date = :date LIMIT 1")
    LiveData<HabitCheckEntity> getHabitCheck(int habitId, String date); // DAO'da bu isim olsun

    @Query("SELECT * FROM habit_checks WHERE date = :date")
    LiveData<List<HabitCheckEntity>> getHabitChecksByDate(String date); // DAO'da bu isim olsun

    @Update
    void update(HabitCheckEntity habitCheck); // DAO’ya ekle

    @Delete
    void delete(HabitCheckEntity habitCheck); // DAO’ya ekle
}

