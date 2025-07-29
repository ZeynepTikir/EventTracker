package com.example.eventtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eventtracker.data.model.HabitCheckEntity;
import com.example.eventtracker.data.repository.HabitCheckRepository;

import java.util.List;

public class HabitCheckViewModel extends AndroidViewModel {

    private final HabitCheckRepository repository;

    public HabitCheckViewModel(@NonNull Application application) {
        super(application);
        repository = new HabitCheckRepository(application);
    }

    public LiveData<HabitCheckEntity> getHabitCheck(int habitId, String date) {
        return repository.getHabitCheck(habitId, date);
    }

    public LiveData<List<HabitCheckEntity>> getHabitChecksByDate(String date) {
        return repository.getHabitChecksByDate(date);
    }

    public LiveData<List<HabitCheckEntity>> getChecksByDate(String date) {
        return repository.getHabitChecksByDate(date);
    }


    public void insert(HabitCheckEntity habitCheck) {
        repository.insert(habitCheck);
    }

    public void update(HabitCheckEntity habitCheck) {
        repository.update(habitCheck);
    }

    public void delete(HabitCheckEntity habitCheck) {
        repository.delete(habitCheck);
    }
}
