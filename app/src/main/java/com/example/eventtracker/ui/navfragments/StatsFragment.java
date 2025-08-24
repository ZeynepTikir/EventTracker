package com.example.eventtracker.ui.navfragments;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventtracker.R;
import com.example.eventtracker.data.AppDatabase;
import com.example.eventtracker.data.dao.PomodoroDao;
import com.example.eventtracker.data.dao.TaskDao;
import com.example.eventtracker.data.model.PomodoroEntity;
import com.example.eventtracker.data.model.PomodoroWithTaskName;
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.ui.adapter.RecentActivityAdapter;
import com.example.eventtracker.utils.ActivityItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatsFragment extends Fragment {

    private static final String TAG = "StatsFragment";

    private TextView tvCompletedTasks, tvTotalPomodoros, tvFocusTime;
    private TaskDao taskDao;
    private PomodoroDao pomodoroDao;
    private RecyclerView recyclerRecentActivity;
    private RecentActivityAdapter adapter;
    private BarChart barChart;
    private boolean isDarkMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        // Tema durumu
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        isDarkMode = sharedPreferences.getBoolean("dark_mode", false);

        // Renkleri uygula
        applyThemeColors(view, isDarkMode);

        tvCompletedTasks = view.findViewById(R.id.tvCompletedTasks);
        tvTotalPomodoros = view.findViewById(R.id.tvTotalPomodoros);
        tvFocusTime = view.findViewById(R.id.tvFocusTime);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        taskDao = db.taskDao();
        pomodoroDao = db.pomodoroDao();

        observeData();

        barChart = new BarChart(requireContext());
        ViewGroup chartContainer = view.findViewById(R.id.chartContainer);
        chartContainer.addView(barChart);
        setupChart();

        recyclerRecentActivity = view.findViewById(R.id.recyclerRecentActivity);
        adapter = new RecentActivityAdapter(new ArrayList<>());
        recyclerRecentActivity.setAdapter(adapter);
        recyclerRecentActivity.setLayoutManager(new LinearLayoutManager(getContext()));

        loadRecentActivities();

        return view;
    }

    private void observeData() {
        taskDao.getCompletedTaskCount().observe(getViewLifecycleOwner(), count ->
                tvCompletedTasks.setText(String.valueOf(count)));

        pomodoroDao.getTotalPomodoros().observe(getViewLifecycleOwner(), total ->
                tvTotalPomodoros.setText(String.valueOf(total)));

        pomodoroDao.getTotalFocusMillis().observe(getViewLifecycleOwner(), totalMs -> {
            long ms = (totalMs != null) ? totalMs : 0L;
            tvFocusTime.setText(formatHM(ms));
        });
    }

    private String formatHM(long millis) {
        long minutes = millis / 60000L;
        long h = minutes / 60;
        long m = minutes % 60;

        if (h > 0 && m > 0) return h + "h " + m + "m";
        else if (h > 0) return h + "h";
        else return m + "m";
    }

    private void setupChart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 2));
        entries.add(new BarEntry(1, 3));
        entries.add(new BarEntry(2, 1));
        entries.add(new BarEntry(3, 4));
        entries.add(new BarEntry(4, 2));
        entries.add(new BarEntry(5, 5));
        entries.add(new BarEntry(6, 3));

        BarDataSet dataSet = new BarDataSet(entries, getString(R.string.focus_time));
        dataSet.setColor(Color.parseColor("#FF9800"));

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);

        barChart.getDescription().setEnabled(false);
        barChart.animateY(800);
    }

    private void loadRecentActivities() {
        new Thread(() -> {
            List<ActivityItem> allActivities = new ArrayList<>();

            // Görevler
            List<TaskEntity> recentTasks = taskDao.getRecentCompletedTasks(10);
            for (TaskEntity task : recentTasks) {
                String title = task.getName();
                String time = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                        .format(new java.util.Date(task.getCompletedTimestamp()));
                String description = getString(R.string.recent_task_item); // "Completed"
                allActivities.add(new ActivityItem(title, time, description, task.getCompletedTimestamp()));
            }

            // Pomodorolar (POJO kullanılıyor)
            List<PomodoroWithTaskName> recentPomodoros = pomodoroDao.getRecentPomodorosWithTaskName();
            for (PomodoroWithTaskName p : recentPomodoros) {
                String title = p.getTaskName(); // artık join ile güncel task adı
                String time = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                        .format(new java.util.Date(p.getTimestamp()));
                String description = getString(R.string.recent_pomodoro_item, p.getTaskName(), p.getDuration() / 60000);
                allActivities.add(new ActivityItem(title, time, description, p.getTimestamp()));
            }

            // En yeni önce sırala
            allActivities.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));

            requireActivity().runOnUiThread(() -> adapter.updateData(allActivities));
        }).start();
    }

    private void applyThemeColors(View root, boolean darkMode) {
        int bgColor = darkMode
                ? getResources().getColor(R.color.dark_background, null)
                : getResources().getColor(R.color.background, null);

        int textColor = darkMode
                ? getResources().getColor(R.color.dark_textcolor, null)
                : getResources().getColor(R.color.textcolor, null);

        int cardColor = darkMode
                ? getResources().getColor(R.color.dark_cardBackground, null)
                : getResources().getColor(R.color.cardBackground, null);

        int dividerColor = darkMode
                ? getResources().getColor(R.color.dark_cancelColor, null)
                : getResources().getColor(R.color.cancelColor, null);

        // Arka plan
        root.setBackgroundColor(bgColor);

        // Tüm TextView'ler için yazı rengini uygula
        setTextColorsRecursively(root, textColor);

        // CardView arka planlarını uygula
        int[] cardIds = {
                R.id.cardCompletedTasks,
                R.id.cardTotalPomodoros,
                R.id.cardFocusTime,
                R.id.chartContainer,
                R.id.cardRecentActivity
        };
        for (int id : cardIds) {
            View v = root.findViewById(id);
            if (v != null) {
                v.setBackgroundTintList(ColorStateList.valueOf(cardColor));
            }
        }

        // item_recent_activity özel ayarlar:
        RecyclerView recycler = root.findViewById(R.id.recyclerRecentActivity);
        if (recycler != null) {
            recycler.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(@NonNull View view) {
                    TextView tvTitle = view.findViewById(R.id.tvTitle);
                    if (tvTitle != null) {
                        tvTitle.setTextColor(textColor);
                    }

                    TextView tvTime = view.findViewById(R.id.tvTime);
                    if (tvTime != null) {
                        tvTime.setTextColor(textColor);
                    }

                    TextView tvDescription = view.findViewById(R.id.tvDescription);
                    if (tvDescription != null) {
                        tvDescription.setTextColor(textColor);

                        int descBg = darkMode
                                ? getResources().getColor(R.color.cancelColor, null)
                                : getResources().getColor(R.color.dark_textcolor, null);
                        tvDescription.setBackgroundTintList(ColorStateList.valueOf(descBg));
                    }
                }

                @Override
                public void onChildViewDetachedFromWindow(@NonNull View view) {}
            });
        }
    }

    // 🔽 Recursive text color setter
    private void setTextColorsRecursively(View view, int textColor) {
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(textColor);
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                setTextColorsRecursively(group.getChildAt(i), textColor);
            }
        }
    }



}
