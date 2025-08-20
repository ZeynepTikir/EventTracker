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
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.ui.adapter.EventAdapter;
import com.example.eventtracker.viewmodel.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private TextView textSelectedDate;
    private RecyclerView recyclerViewEvents;
    private EventAdapter eventAdapter;
    private TaskViewModel taskViewModel;

    private String selectedDate;

    private final SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public CalendarFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        textSelectedDate = view.findViewById(R.id.textSelectedDate);
        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);

        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // İlk açılışta bugünün tarihi
        selectedDate = dbFormat.format(new Date(calendarView.getDate()));
        textSelectedDate.setText("Seçilen Gün: " + displayFormat.format(new Date(calendarView.getDate())));

        // Adapter oluştur
        eventAdapter = new EventAdapter(new ArrayList<>(), taskViewModel);
        recyclerViewEvents.setAdapter(eventAdapter);

        observeTasksByDate(selectedDate);

        // Tarih seçilince
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            textSelectedDate.setText("Seçilen Gün: " + dayOfMonth + "/" + (month + 1) + "/" + year);

            observeTasksByDate(selectedDate);
        });

        return view;
    }

    private void observeTasksByDate(String date) {
        taskViewModel.getTasksByDate(date).observe(getViewLifecycleOwner(), tasks -> {
            List<TaskEntity> taskList = tasks != null ? tasks : new ArrayList<>();
            eventAdapter.setTaskList(taskList);
        });
    }
}
