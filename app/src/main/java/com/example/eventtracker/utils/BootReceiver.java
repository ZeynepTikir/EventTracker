package com.example.eventtracker.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.eventtracker.data.AppDatabase;
import com.example.eventtracker.data.dao.TaskDao;
import com.example.eventtracker.data.model.TaskEntity;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            TaskDao taskDao = AppDatabase.getInstance(context).taskDao();
            List<TaskEntity> tasks = taskDao.getAllTasksSync();

            for (TaskEntity task : tasks) {
                AlarmHelper.scheduleTaskReminder(context, task);
            }
        }
    }
}

