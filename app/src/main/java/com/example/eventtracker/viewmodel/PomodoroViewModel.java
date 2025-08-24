package com.example.eventtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventtracker.data.AppDatabase;
import com.example.eventtracker.data.model.DayPomodoroStats;
import com.example.eventtracker.data.repository.PomodoroRepository;

import java.util.List;

public class PomodoroViewModel extends AndroidViewModel {
    private PomodoroRepository repository;
    private MutableLiveData<List<DayPomodoroStats>> weeklyStats = new MutableLiveData<>();

    public PomodoroViewModel(@NonNull Application application) {
        super(application);
        repository = new PomodoroRepository(AppDatabase.getInstance(application).pomodoroDao());
    }

    public LiveData<List<DayPomodoroStats>> getWeeklyStats() {
        return weeklyStats;
    }

}


