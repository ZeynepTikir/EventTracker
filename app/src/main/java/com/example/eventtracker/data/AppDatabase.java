package com.example.eventtracker.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.eventtracker.data.dao.HabitCheckDao;
import com.example.eventtracker.data.dao.TaskDao;
import com.example.eventtracker.data.dao.HabitDao;
import com.example.eventtracker.data.model.HabitCheckEntity;
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.data.model.HabitEntity;

@Database(entities = {TaskEntity.class, HabitEntity.class, HabitCheckEntity.class}, version = 4, exportSchema = false)

@TypeConverters({Converters.class}) // 🔴 BURASI MUTLAKA EKLENECEK
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract TaskDao taskDao();
    public abstract HabitDao habitDao();
    public abstract HabitCheckDao habitCheckDao();


    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "event_tracker_database"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}