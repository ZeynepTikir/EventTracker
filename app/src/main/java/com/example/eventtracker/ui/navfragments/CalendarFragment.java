package com.example.eventtracker.ui.navfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventtracker.R;
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.ui.adapter.EventAdapter;
import com.example.eventtracker.ui.task.EditTaskBottomSheetFragment;
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

        applyThemeColors(view);

        calendarView = view.findViewById(R.id.calendarView);
        textSelectedDate = view.findViewById(R.id.textSelectedDate);
        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);

        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // İlk açılışta bugünün tarihi
        selectedDate = dbFormat.format(new Date(calendarView.getDate()));
        String today = displayFormat.format(new Date(calendarView.getDate()));
        textSelectedDate.setText(getString(R.string.selected_day, today));

        // Adapter oluştur
        eventAdapter = new EventAdapter(new ArrayList<>(), taskViewModel);
        recyclerViewEvents.setAdapter(eventAdapter);

        setupEventListeners();

        observeTasksByDate(selectedDate);

        // Tarih seçilince
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            String formattedDate = getString(R.string.selected_day_format, dayOfMonth, month + 1, year);
            textSelectedDate.setText(getString(R.string.selected_day, formattedDate));

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

    private void applyThemeColors(View rootView) {
        boolean darkMode = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean("dark_mode", false);

        int backgroundColor = ContextCompat.getColor(requireContext(),
                darkMode ? R.color.dark_background : R.color.background);
        int cardColor = ContextCompat.getColor(requireContext(),
                darkMode ? R.color.dark_cardBackground : R.color.cardBackground);
        int textColor = ContextCompat.getColor(requireContext(),
                darkMode ? R.color.dark_textcolor : R.color.textcolor);

        // Sayfa arka planı
        rootView.setBackgroundColor(backgroundColor);

        // Başlık ve alt yazılar
        TextView title = rootView.findViewById(R.id.title);
        TextView selectedDate = rootView.findViewById(R.id.textSelectedDate);
        TextView plannedTitle = rootView.findViewById(R.id.plannedTitle);

        if (title != null) title.setTextColor(textColor);
        if (selectedDate != null) selectedDate.setTextColor(textColor);
        if (plannedTitle != null) plannedTitle.setTextColor(textColor);

        // CardView arka planı
        CardView calendarCard = rootView.findViewById(R.id.calendarCard);
        if (calendarCard != null) {
            calendarCard.setCardBackgroundColor(cardColor);
        }

        // CalendarView renkleri
        CalendarView calendar = rootView.findViewById(R.id.calendarView);
        if (calendar != null) {
            //Text colors
            calendar.setWeekDayTextAppearance(darkMode ? R.style.CalenderViewWeekCustomTextDark : R.style.CalenderViewWeekCustomText);
            calendar.setDateTextAppearance(darkMode ? R.style.CalenderViewDateCustomTextDark : R.style.CalenderViewDateCustomText);
        }

        View header = rootView.findViewById(R.id.calendarHeader);
        if (header != null) {
            int headerColor = ContextCompat.getColor(requireContext(),
                    darkMode ? R.color.dark_calendarHeaderBackground : R.color.calendarHeaderBackground);
            header.setBackgroundColor(headerColor);
        }

    }
    private void setupEventListeners() {
        eventAdapter.setOnItemClickListener(task -> {
            new EditTaskBottomSheetFragment(task)
                    .show(getParentFragmentManager(), "EditTaskBottomSheet");
        });
    }
}
