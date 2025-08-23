package com.example.eventtracker.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.example.eventtracker.R;

import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskName = intent.getStringExtra("taskName");

        String lang = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("language", "en");

        // SharedPreferences yerine LocaleHelper ile güncel dili al
        Context localizedContext = LocaleHelper.setLocale(context, lang);

        // Log.d("AlarmReceiver", "Language preference: " + lang);
        // Log.d("AlarmReceiver", "Task name: " + taskName);

        // Android 13+ için izin kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            // İzin yok, bildirimi göndermiyoruz
            Log.d("AlarmReceiver", "Notification permission not granted");
            return;
        }

        Log.d("AlarmReceiver", "Notification permission granted");
        String title = localizedContext.getString(R.string.notification_task_title);
        String text = localizedContext.getString(R.string.notification_task_text, taskName);

        // Log.d("AlarmReceiver", "Notification title: " + title + ", text: " + text);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(localizedContext, "task_channel")
                .setSmallIcon(R.drawable.ic_notify)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(localizedContext);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}


