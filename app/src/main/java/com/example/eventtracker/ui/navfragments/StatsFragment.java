package com.example.eventtracker.ui.navfragments;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventtracker.R;
import com.example.eventtracker.data.AppDatabase;
import com.example.eventtracker.data.dao.PomodoroDao;
import com.example.eventtracker.data.dao.TaskDao;
import com.example.eventtracker.data.model.DayPomodoroStats;
import com.example.eventtracker.data.model.PomodoroWithTaskName;
import com.example.eventtracker.data.model.TaskDayStats;
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.ui.adapter.RecentActivityAdapter;
import com.example.eventtracker.utils.ActivityItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private static final String TAG = "StatsFragment";

    private TextView tvCompletedTasks, tvTotalPomodoros, tvFocusTime;
    private TaskDao taskDao;
    private PomodoroDao pomodoroDao;
    private RecyclerView recyclerRecentActivity;
    private RecentActivityAdapter adapter;
    private CombinedChart combinedChart;
    private BarChart taskChart;
    private LineChart pomoChart;
    private TextView btnTasks, btnPomodoros;
    private CardView cardTaskChart, cardPomoChart;
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

        taskChart = view.findViewById(R.id.chartTasks);
        pomoChart = view.findViewById(R.id.chartPomodoro);
        btnTasks = view.findViewById(R.id.btnShowTasks);
        btnPomodoros = view.findViewById(R.id.btnShowPomodoro);
        cardTaskChart = view.findViewById(R.id.cardTaskChart);
        cardPomoChart = view.findViewById(R.id.cardPomodoroChart);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        taskDao = db.taskDao();
        pomodoroDao = db.pomodoroDao();

        observeData();
        loadWeeklyStats();

        recyclerRecentActivity = view.findViewById(R.id.recyclerRecentActivity);
        adapter = new RecentActivityAdapter(new ArrayList<>());
        recyclerRecentActivity.setAdapter(adapter);
        recyclerRecentActivity.setLayoutManager(new LinearLayoutManager(getContext()));

        loadRecentActivities();

        updateButtonStates(true);

        btnTasks.setOnClickListener(v -> {
            cardTaskChart.setVisibility(View.VISIBLE);
            cardPomoChart.setVisibility(View.GONE);
            updateButtonStates(true);
        });

        btnPomodoros.setOnClickListener(v -> {
            cardTaskChart.setVisibility(View.GONE);
            cardPomoChart.setVisibility(View.VISIBLE);
            updateButtonStates(false);
        });

        // default: görevler açık
        cardTaskChart.setVisibility(View.VISIBLE);
        cardPomoChart.setVisibility(View.GONE);

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

    private void loadWeeklyStats() {
        long weekStart = getStartOfWeek();

        new Thread(() -> {
            List<TaskDayStats> taskStats = taskDao.getWeeklyTaskStats(weekStart);
            List<DayPomodoroStats> pomoStats = pomodoroDao.getWeeklyPomodoroStats(weekStart);

            requireActivity().runOnUiThread(() -> {
                setupTaskChart(taskStats, isDarkMode);

                setupPomoChart(pomoStats, isDarkMode);
            });
        }).start();
    }

    private long getStartOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private void setupTaskChart(List<TaskDayStats> taskStats, boolean darkMode) {
        int textColor = darkMode
                ? getResources().getColor(R.color.dark_textcolor, null)
                : getResources().getColor(R.color.textcolor, null);

        int completedColor = darkMode
                ? getResources().getColor(R.color.chartColor1)
                : getResources().getColor(R.color.button);

        int remainingColor = darkMode
                ? getResources().getColor(R.color.calendarHeaderBackground)
                : getResources().getColor(R.color.chartColor2);

        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(i, new float[]{0, 0}));
        }

        for (TaskDayStats stat : taskStats) {
            float completed = stat.completedTasks;
            float remaining = stat.totalTasks - stat.completedTasks;
            entries.set(stat.dayIndex, new BarEntry(stat.dayIndex, new float[]{completed, remaining}));
        }

        BarDataSet set = new BarDataSet(entries,"");
        set.setColors(completedColor, remainingColor);  // burada tema renklerini kullan
        set.setStackLabels(new String[]{
                getString(R.string.completed_label),
                getString(R.string.remaining_label)
        });
        set.setValueTextColor(textColor);

        // ✅ Sütun üstündeki değerler için formatter
        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value == 0f ? "" : String.valueOf((int) value);
            }
        });

        // Y-ekseni formatter
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        };

        YAxis leftAxis = taskChart.getAxisLeft();
        leftAxis.setValueFormatter(formatter);
        leftAxis.setGranularity(1f);
        leftAxis.setGranularityEnabled(true);

        BarData data = new BarData(set);
        data.setBarWidth(0.5f);
        taskChart.setData(data);

        styleXAxis(taskChart.getXAxis());
        styleYAxis(taskChart.getAxisLeft());
        taskChart.getAxisRight().setEnabled(false);
        taskChart.getXAxis().setTextColor(textColor);
        taskChart.getAxisLeft().setTextColor(textColor);

        Legend legend = taskChart.getLegend();
        legend.setTextColor(textColor);

        // 🔑 kenar boşlukları ve legend hizalaması
        styleChartMargins(taskChart, true);

        taskChart.getDescription().setEnabled(false);
        taskChart.animateY(600);
        taskChart.invalidate();
    }

    private void setupPomoChart(List<DayPomodoroStats> pomoStats, boolean darkMode) {
        int textColor = darkMode
                ? getResources().getColor(R.color.dark_textcolor, null)
                : getResources().getColor(R.color.textcolor, null);

        int lineColor = darkMode
                ? getResources().getColor(R.color.chartColor2)
                : getResources().getColor(R.color.button);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) entries.add(new Entry(i, 0));

        for (DayPomodoroStats stat : pomoStats) {
            entries.set(stat.dayIndex, new Entry(stat.dayIndex, stat.totalDuration / 60000f));
        }

        LineDataSet set = new LineDataSet(entries, getString(R.string.pomodoro_label));
        set.setColor(lineColor);     // tema rengi
        set.setCircleColor(lineColor);
        set.setLineWidth(0.6f);
        set.setCircleRadius(5f);
        set.setValueTextColor(textColor);

        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value == 0f ? "" : String.valueOf((int) value);
            }
        });

        LineData data = new LineData(set);
        pomoChart.setData(data);

        styleXAxis(pomoChart.getXAxis());
        styleYAxis(pomoChart.getAxisLeft());
        pomoChart.getAxisRight().setEnabled(false);
        pomoChart.getXAxis().setTextColor(textColor);
        pomoChart.getAxisLeft().setTextColor(textColor);

        Legend legendPomo = pomoChart.getLegend();
        legendPomo.setTextColor(textColor);

        // 🔑 kenar boşlukları ve legend hizalaması
        styleChartMargins(pomoChart, false);

        pomoChart.getDescription().setEnabled(false);
        pomoChart.animateY(600);
        pomoChart.invalidate();
    }


    private void styleXAxis(XAxis xAxis) {
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);

        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(6.5f);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int d = (int) value;
                switch (d) {
                    case 0: return getString(R.string.mon);
                    case 1: return getString(R.string.tue);
                    case 2: return getString(R.string.wed);
                    case 3: return getString(R.string.thu);
                    case 4: return getString(R.string.fri);
                    case 5: return getString(R.string.sat);
                    case 6: return getString(R.string.sun);
                    default: return "";
                }
            }
        });
    }


    private void styleYAxis(YAxis yAxis) {
        yAxis.setAxisMinimum(0f);
    }

    private void styleChartMargins(com.github.mikephil.charting.charts.Chart<?> chart, boolean barchart) {
        // Grafik kenar boşlukları
        chart.setExtraBottomOffset(10f); // Alt boşluk
        chart.setExtraTopOffset(12f);
        chart.setExtraLeftOffset(20f);
        chart.setExtraRightOffset(10f);

        // Legend ayarları
        Legend legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(10f);
        legend.setXOffset(-10f);

        if(barchart)
            legend.setYOffset(13f);
        else
            legend.setYOffset(8f);
    }

    private void updateButtonStates(boolean tasksActive) {
        if(tasksActive) {
            btnTasks.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button)));
            btnTasks.setTextColor(getResources().getColor(R.color.buttonText));
            btnPomodoros.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_cancelColor)));
            btnPomodoros.setTextColor(getResources().getColor(R.color.textcolor));
        } else {
            btnTasks.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_cancelColor)));
            btnTasks.setTextColor(getResources().getColor(R.color.textcolor));
            btnPomodoros.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button)));
            btnPomodoros.setTextColor(getResources().getColor(R.color.buttonText));
        }
    }


    private void loadRecentActivities() {
        new Thread(() -> {
            List<ActivityItem> allActivities = new ArrayList<>();

            List<TaskEntity> recentTasks = taskDao.getRecentCompletedTasks(10);
            for (TaskEntity task : recentTasks) {
                String title = task.getName();
                String time = new SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(task.getCompletedTimestamp());
                String description = getString(R.string.recent_task_item);
                allActivities.add(new ActivityItem(title, time, description, task.getCompletedTimestamp()));
            }

            List<PomodoroWithTaskName> recentPomodoros = pomodoroDao.getRecentPomodorosWithTaskName();
            for (PomodoroWithTaskName p : recentPomodoros) {
                String title = p.getTaskName();
                String time = new SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(p.getTimestamp());
                String description = getString(R.string.recent_pomodoro_item, p.getTaskName(), p.getDuration() / 60000);
                allActivities.add(new ActivityItem(title, time, description, p.getTimestamp()));
            }

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

        // Arka plan
        root.setBackgroundColor(bgColor);

        // Tüm TextView'ler için yazı rengini uygula
        setTextColorsRecursively(root, textColor);

        // CardView arka planlarını uygula
        int[] cardIds = {
                R.id.cardCompletedTasks,
                R.id.cardTotalPomodoros,
                R.id.cardFocusTime,
                R.id.cardTaskChart,
                R.id.cardPomodoroChart,
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
