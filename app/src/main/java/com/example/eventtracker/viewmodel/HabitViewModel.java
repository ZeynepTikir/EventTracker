package com.example.eventtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eventtracker.data.model.HabitEntity;
import com.example.eventtracker.data.model.HabitWithCheck;
import com.example.eventtracker.data.repository.HabitRepository;

import java.util.List;

public class HabitViewModel extends AndroidViewModel {
    private final HabitRepository repository;
    private final LiveData<List<HabitEntity>> allHabits;

    public HabitViewModel(@NonNull Application application) {
        super(application);
        repository = new HabitRepository(application);
        allHabits = repository.getAllHabits();
    }

    public void insert(HabitEntity habit) {
        repository.insert(habit);
    }

    public void update(HabitEntity habit) {
        repository.update(habit);
    }

    public void delete(HabitEntity habit) {
        repository.delete(habit);
    }



    public LiveData<List<HabitWithCheck>> getHabitsWithChecksForDate(String date) {
        return repository.getHabitsWithChecksForDate(date);
    }


    public LiveData<List<HabitEntity>> getAllHabits() {
        return allHabits;
    }
}
