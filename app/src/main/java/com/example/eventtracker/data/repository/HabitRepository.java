package com.example.eventtracker.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.eventtracker.data.AppDatabase;
import com.example.eventtracker.data.dao.HabitDao;
import com.example.eventtracker.data.model.HabitEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HabitRepository {
    private final HabitDao habitDao;
    private final ExecutorService executorService;

    public HabitRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        habitDao = db.habitDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(HabitEntity habit) {
        executorService.execute(() -> habitDao.insert(habit));
    }

    public void update(HabitEntity habit) {
        executorService.execute(() -> habitDao.update(habit));
    }

    public void delete(HabitEntity habit) {
        executorService.execute(() -> habitDao.delete(habit));
    }

    public LiveData<List<HabitEntity>> getAllHabits() {
        return habitDao.getAllHabits();
    }
}
