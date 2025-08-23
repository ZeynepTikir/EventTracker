package com.example.eventtracker.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.utils.AlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmHelper {

    public static void scheduleTaskReminder(Context context, TaskEntity task) {
        // Tarih + saat formatı
        String dateTime = task.getDate() + " " + task.getTime(); // "yyyy-MM-dd HH:mm"

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        long triggerTime;

        try {
            triggerTime = sdf.parse(dateTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        // Geçmiş zaman ise alarm kurma
        if (triggerTime < System.currentTimeMillis()) return;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("taskName", task.getName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                task.getId(), // benzersiz: DB id’si
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }
}
