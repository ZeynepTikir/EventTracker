package com.example.eventtracker.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.eventtracker.data.AppDatabase;
import com.example.eventtracker.data.dao.TaskDao;
import com.example.eventtracker.data.model.TaskEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import android.util.Log;

public class TaskRepository {
    private final TaskDao taskDao;
    private final ExecutorService executorService;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        taskDao = db.taskDao();
        executorService = Executors.newSingleThreadExecutor();
    }


    public long insert(TaskEntity task) {
        Callable<Long> callable = () -> taskDao.insert(task);
        Future<Long> future = executorService.submit(callable);
        try {
            return future.get(); // DB insert tamamlanana kadar bekler ve id döner
        } catch (ExecutionException | InterruptedException e) {
            Log.e("TaskRepository", "Failed to insert task", e);
            return -1;
        }
    }

    public void update(TaskEntity task) {
        executorService.execute(() -> taskDao.update(task));
    }

    public void updateChecked(int id, boolean checked) {
        long timestamp = checked ? System.currentTimeMillis() : 0L;
        executorService.execute(() -> taskDao.updateChecked(id, checked, timestamp));
    }


    public void delete(TaskEntity task) {
        executorService.execute(() -> taskDao.delete(task));
    }

    public LiveData<List<TaskEntity>> getAllTasks() {
        return taskDao.getAllTasks();
    }


    // 🔹 Yeni fonksiyon
    public LiveData<List<TaskEntity>> getActiveUncheckedTasks() {
        return taskDao.getActiveUncheckedTasks();
    }

    public LiveData<List<TaskEntity>> getTodayTasks(String todayDate) {
        return taskDao.getTasksByDate(todayDate);
    }

    public LiveData<List<TaskEntity>> getTasksByDate(String date) {
        return taskDao.getTasksByDate(date);
    }

}
