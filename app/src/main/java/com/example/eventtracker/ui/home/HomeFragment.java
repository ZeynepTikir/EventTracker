package com.example.eventtracker.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventtracker.R;
import com.example.eventtracker.data.model.HabitEntity;
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.ui.AddSelectionBottomSheet;
import com.example.eventtracker.ui.adapter.EventAdapter;
import com.example.eventtracker.ui.adapter.EventItem;
import com.example.eventtracker.ui.habit.NewHabitFragment;
import com.example.eventtracker.ui.task.NewTaskFragment;
import com.example.eventtracker.viewmodel.HabitViewModel;
import com.example.eventtracker.viewmodel.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<EventItem> eventList = new ArrayList<>();

    private TaskViewModel taskViewModel;
    private HabitViewModel habitViewModel;

    private List<TaskEntity> currentTasks = new ArrayList<>();
    private List<HabitEntity> currentHabits = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventAdapter = new EventAdapter(eventList);
        recyclerView.setAdapter(eventAdapter);

        View icAdd = view.findViewById(R.id.ic_add);
        icAdd.setOnClickListener(v -> {
            AddSelectionBottomSheet bottomSheet = new AddSelectionBottomSheet();

            bottomSheet.setOnSelectionListener(new AddSelectionBottomSheet.OnSelectionListener() {
                @Override
                public void onAddTaskSelected() {
                    NewTaskFragment newTaskFragment = new NewTaskFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, newTaskFragment)
                            .addToBackStack(null)
                            .commit();
                }

                @Override
                public void onAddHabitSelected() {
                    NewHabitFragment newHabitFragment = new NewHabitFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, newHabitFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            bottomSheet.show(getParentFragmentManager(), "AddSelectionBottomSheet");
        });

        setupViewModels();

        return view;
    }

    private void setupViewModels() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);

        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            currentTasks = tasks;
            updateEventList();
        });

        habitViewModel.getAllHabits().observe(getViewLifecycleOwner(), habits -> {
            currentHabits = habits;
            updateEventList();for (HabitEntity habit : habits) {
                Log.d("HabitDebug", "Habit adı: " + habit.getName());
                boolean[] days = habit.getDays();

                if (days != null && days.length == 7) {
                    StringBuilder sb = new StringBuilder();
                    String[] dayNames = {"Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz"};

                    for (int i = 0; i < 7; i++) {
                        sb.append(dayNames[i]).append(": ").append(days[i] ? "✔️" : "❌").append("  ");
                    }

                    Log.d("HabitDebug", "Aktif günler -> " + sb.toString());
                } else {
                    Log.d("HabitDebug", "Gün bilgisi eksik!");
                }
            }
        });
    }

    private void updateEventList() {
        eventList.clear();
        String today = getTodayDate(); // "yyyy-MM-dd"

        // Bugünün task'leri
        android.util.Log.d("HomeFragment", "----- Tasks for today (" + today + ") -----");
        for (TaskEntity task : currentTasks) {
            if (task.getDate() != null && task.getDate().equals(today)) {
                eventList.add(new EventItem(task));
                android.util.Log.d("HomeFragment", "Task: " + task.getName() + " at " + task.getTime());
            }
        }

        // Bugünkü habit'ler
        android.util.Log.d("HomeFragment", "----- Habits -----");
        for (HabitEntity habit : currentHabits) {
            boolean show = habit.isScheduledForToday();
            android.util.Log.d("HomeFragment", "Habit: " + habit.getName() + ", showToday: " + show);
            if (show) {
                eventList.add(new EventItem(habit));
            }
        }

        // Saat sırasına göre sırala
        Collections.sort(eventList, (e1, e2) -> {
            String time1 = e1.getType() == EventItem.TYPE_TASK
                    ? e1.getTask().getTime()
                    : e1.getHabit().getTime();

            String time2 = e2.getType() == EventItem.TYPE_TASK
                    ? e2.getTask().getTime()
                    : e2.getHabit().getTime();

            return compareTimeStrings(time1, time2);
        });

        android.util.Log.d("HomeFragment", "Total events to show: " + eventList.size());

        eventAdapter.notifyDataSetChanged();
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private int compareTimeStrings(String t1, String t2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date d1 = sdf.parse(t1);
            Date d2 = sdf.parse(t2);
            return d1.compareTo(d2);
        } catch (Exception e) {
            return t1.compareTo(t2); // Parse edemezsek string karşılaştırması
        }
    }


}
