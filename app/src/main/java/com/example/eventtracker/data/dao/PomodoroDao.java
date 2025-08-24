package com.example.eventtracker.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import com.example.eventtracker.data.model.DayPomodoroStats;
import com.example.eventtracker.data.model.PomodoroEntity;
import com.example.eventtracker.data.model.PomodoroWithTaskName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Dao
public interface PomodoroDao {

    @Insert
    long insert(PomodoroEntity pomodoro);

    @Update
    void update(PomodoroEntity pomodoro);

    @Delete
    void delete(PomodoroEntity pomodoro);

    @Query("SELECT * FROM pomodoros ORDER BY timestamp DESC")
    List<PomodoroEntity> getAllPomodoros();

    @Query("SELECT * FROM pomodoros WHERE taskId = :taskId ORDER BY timestamp DESC")
    List<PomodoroEntity> getPomodorosForTask(int taskId);

    @Query("SELECT COUNT(*) FROM pomodoros")
    LiveData<Integer> getTotalPomodoros();

    @Query("SELECT * FROM pomodoros ORDER BY timestamp DESC LIMIT 10")
    List<PomodoroEntity> getRecentPomodoros();


    @Query("SELECT COALESCE(SUM(duration), 0) FROM pomodoros")
    LiveData<Long> getTotalFocusMillis();

    @Query("SELECT p.id, p.taskId, t.name AS taskName, p.duration, p.completed, p.timestamp " +
            "FROM pomodoros p INNER JOIN tasks t ON p.taskId = t.id " +
            "ORDER BY p.timestamp DESC")
    List<PomodoroWithTaskName> getRecentPomodorosWithTaskName();

    @Query("SELECT * FROM pomodoros ORDER BY timestamp ASC")
    List<PomodoroEntity> getAllPomodorosSync();

    // Haftalık istatistikler: task'taki gibi
    default List<DayPomodoroStats> getWeeklyPomodoroStats(long weekStartMillis) {
        List<DayPomodoroStats> stats = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(weekStartMillis);
        long weekEndMillis = weekStartMillis + 7 * 24 * 60 * 60 * 1000 - 1;

        List<PomodoroEntity> all = getAllPomodorosSync();

        // 7 günlük array ile toplam süreleri tut
        long[] dailyTotals = new long[7];

        for (PomodoroEntity p : all) {
            if (p.getTimestamp() >= weekStartMillis && p.getTimestamp() <= weekEndMillis) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(p.getTimestamp());
                int dayIndex = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7; // Pazartesi=0 … Pazar=6
                dailyTotals[dayIndex] += p.getDuration(); // duration ms cinsinden
            }
        }

        for (int i = 0; i < 7; i++) {
            stats.add(new DayPomodoroStats(i, dailyTotals[i]));
        }
        return stats;
    }

}
