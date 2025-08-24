package com.example.eventtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.data.repository.TaskRepository;
import com.example.eventtracker.utils.AlarmHelper;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository repository;
    private final LiveData<List<TaskEntity>> allTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTasks();
    }

    public long insert(TaskEntity task) { return repository.insert(task); }

    public void update(TaskEntity task) {
        repository.update(task);
    }

    public void delete(TaskEntity task) {
        repository.delete(task);
    }

    public LiveData<List<TaskEntity>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<TaskEntity>> getActiveUncheckedTasks() {
        return repository.getActiveUncheckedTasks();
    }

    public LiveData<List<TaskEntity>> getTodayTasks(String todayDate) {
        return repository.getTodayTasks(todayDate);
    }

    public void updateTaskChecked(int id, boolean checked) {
        repository.updateChecked(id, checked);
    }

    public LiveData<List<TaskEntity>> getTasksByDate(String date) {
        return repository.getTasksByDate(date);
    }

}
