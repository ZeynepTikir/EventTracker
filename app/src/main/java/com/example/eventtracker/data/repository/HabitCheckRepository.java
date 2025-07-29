package com.example.eventtracker.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.eventtracker.data.AppDatabase;
import com.example.eventtracker.data.dao.HabitCheckDao;
import com.example.eventtracker.data.model.HabitCheckEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HabitCheckRepository {

    private final HabitCheckDao habitCheckDao;
    private final ExecutorService executorService;

    public HabitCheckRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        habitCheckDao = db.habitCheckDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<HabitCheckEntity> getHabitCheck(int habitId, String date) {
        return habitCheckDao.getHabitCheck(habitId, date);
    }

    public LiveData<List<HabitCheckEntity>> getHabitChecksByDate(String date) {
        return habitCheckDao.getHabitChecksByDate(date);
    }



    public void insert(HabitCheckEntity habitCheck) {
        executorService.execute(() -> habitCheckDao.insert(habitCheck));
    }

    public void update(HabitCheckEntity habitCheck) {
        executorService.execute(() -> habitCheckDao.update(habitCheck));
    }

    public void delete(HabitCheckEntity habitCheck) {
        executorService.execute(() -> habitCheckDao.delete(habitCheck));
    }

}
