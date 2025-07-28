package com.example.eventtracker.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.eventtracker.data.AppDatabase;
import com.example.eventtracker.data.dao.TaskDao;
import com.example.eventtracker.data.model.TaskEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {
    private final TaskDao taskDao;
    private final ExecutorService executorService;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        taskDao = db.taskDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(TaskEntity task) {
        executorService.execute(() -> taskDao.insert(task));
    }

    public void update(TaskEntity task) {
        executorService.execute(() -> taskDao.update(task));
    }

    public void updateChecked(int id, boolean checked) {
        executorService.execute(() -> taskDao.updateChecked(id, checked));
    }


    public void delete(TaskEntity task) {
        executorService.execute(() -> taskDao.delete(task));
    }

    public LiveData<List<TaskEntity>> getAllTasks() {
        return taskDao.getAllTasks();
    }

    public LiveData<List<TaskEntity>> getTodayTasks(String todayDate) {
        return taskDao.getTasksByDate(todayDate);
    }
}
