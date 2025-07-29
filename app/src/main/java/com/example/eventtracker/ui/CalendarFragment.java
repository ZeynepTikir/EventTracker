package com.example.eventtracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventtracker.R;
import com.example.eventtracker.data.model.HabitCheckEntity;
import com.example.eventtracker.data.model.HabitEntity;
import com.example.eventtracker.data.model.HabitWithCheck;
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.ui.adapter.EventAdapter;
import com.example.eventtracker.ui.adapter.EventItem;
import com.example.eventtracker.viewmodel.HabitCheckViewModel;
import com.example.eventtracker.viewmodel.HabitViewModel;
import com.example.eventtracker.viewmodel.TaskViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Takvim sayfası
public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private TextView textSelectedDate;
    private RecyclerView recyclerViewEvents;
    private EventAdapter eventAdapter;
    private TaskViewModel taskViewModel;
    private HabitViewModel habitViewModel;
    private HabitCheckViewModel habitCheckViewModel;

    private String selectedDate;

    private final SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public CalendarFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);




        calendarView = view.findViewById(R.id.calendarView);
        textSelectedDate = view.findViewById(R.id.textSelectedDate);
        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);

        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        habitCheckViewModel = new ViewModelProvider(requireActivity()).get(HabitCheckViewModel.class);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);

        selectedDate = dbFormat.format(new Date(calendarView.getDate()));
        textSelectedDate.setText("Seçilen Gün: " + displayFormat.format(new Date(calendarView.getDate())));

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        eventAdapter = new EventAdapter(new ArrayList<>(), habitCheckViewModel, getViewLifecycleOwner(), today);
        recyclerViewEvents.setAdapter(eventAdapter);

        observeEventsByDate(selectedDate);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String dateString = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            selectedDate = dateString;

            textSelectedDate.setText("Seçilen Gün: " + dayOfMonth + "/" + (month + 1) + "/" + year);

            eventAdapter.setSelectedDate(selectedDate);
            eventAdapter.notifyDataSetChanged();

            observeEventsByDate(selectedDate);
        });

        return view;
    }

    private void observeEventsByDate(String date) {
        taskViewModel.getTasksByDate(date).observe(getViewLifecycleOwner(), tasks -> {
            habitViewModel.getHabitsWithChecksForDate(date).observe(getViewLifecycleOwner(), habitsWithChecks -> {
                List<EventItem> items = new ArrayList<>();

                // Haftanın günü
                try {
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    calendar.setTime(format.parse(date));
                    int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);
                    int dayIndex = (dayOfWeek + 5) % 7;

                    // Görevler
                    if (tasks != null) {
                        for (TaskEntity task : tasks) {
                            items.add(new EventItem(task));
                        }
                    }

                    // Alışkanlıklar sadece ilgili güne göre
                    if (habitsWithChecks != null) {
                        for (var hwc : habitsWithChecks) {
                            HabitEntity habit = hwc.getHabit();
                            HabitCheckEntity check = hwc.getCheck();
                            boolean isChecked = check != null && check.isChecked();

                            if (habit.getDays() != null && habit.getDays().length == 7 && habit.getDays()[dayIndex]) {
                                items.add(new EventItem(habit, isChecked));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                eventAdapter.setEventList(items);
            });
        });
    }
}
