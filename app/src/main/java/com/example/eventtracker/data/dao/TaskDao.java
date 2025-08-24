package com.example.eventtracker.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.eventtracker.data.model.TaskDayStats;
import com.example.eventtracker.data.model.TaskEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Dao
public interface TaskDao {

    @Insert
    long insert(TaskEntity task);

    @Update
    void update(TaskEntity task);

    @Delete
    void delete(TaskEntity task);

    @Query("DELETE FROM tasks")
    void deleteAllTasks();

    @Query("SELECT * FROM tasks ORDER BY time ASC")
    LiveData<List<TaskEntity>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE checked = 0 ORDER BY time ASC")
    LiveData<List<TaskEntity>> getActiveUncheckedTasks();

    @Query("SELECT * FROM tasks ORDER BY time ASC")
    List<TaskEntity> getAllTasksSync();  // LiveData değil, direkt liste döner


    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY time ASC")
    LiveData<List<TaskEntity>> getTasksByDate(String date);


    @Query("UPDATE tasks SET checked = :checked, completedTimestamp = :completedTimestamp WHERE id = :id")
    void updateChecked(int id, boolean checked, long completedTimestamp);

    @Query("SELECT COUNT(*) FROM tasks WHERE checked = 1")
    LiveData<Integer> getCompletedTaskCount();

    // Son tamamlanan tasklar: timestamp sırasına göre
    @Query("SELECT * FROM tasks WHERE checked = 1 ORDER BY completedTimestamp DESC LIMIT :limit")
    List<TaskEntity> getRecentCompletedTasks(int limit);


    @Query("SELECT * FROM tasks WHERE date BETWEEN :startDate AND :endDate")
    List<TaskEntity> getTasksBetweenDates(String startDate, String endDate);

    // Haftalık istatistikleri dönmek için
    default List<TaskDayStats> getWeeklyTaskStats(long weekStartMillis) {
        List<TaskDayStats> stats = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(weekStartMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            String day = sdf.format(cal.getTime());
            List<TaskEntity> tasks = getTasksBetweenDates(day, day);
            float total = tasks.size();
            float completed = (float) tasks.stream().filter(TaskEntity::isChecked).count();
            stats.add(new TaskDayStats(i, total, completed));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return stats;
    }
}
