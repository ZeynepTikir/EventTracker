package com.example.eventtracker.ui.home;

import android.os.Bundle;
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
import com.example.eventtracker.data.model.HabitCheckEntity;
import com.example.eventtracker.data.model.HabitEntity;
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.ui.AddSelectionBottomSheet;
import com.example.eventtracker.ui.adapter.EventAdapter;
import com.example.eventtracker.ui.adapter.EventItem;
import com.example.eventtracker.ui.habit.EditHabitBottomSheetFragment;
import com.example.eventtracker.ui.habit.NewHabitFragment;
import com.example.eventtracker.ui.task.EditTaskBottomSheetFragment;
import com.example.eventtracker.ui.task.NewTaskFragment;
import com.example.eventtracker.viewmodel.HabitCheckViewModel;
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
    private HabitCheckViewModel habitCheckViewModel;

    private String selectedDate = getTodayDate(); // başlangıçta bugünün tarihi

    private List<TaskEntity> currentTasks = new ArrayList<>();
    private List<HabitEntity> currentHabits = new ArrayList<>();
    private List<HabitCheckEntity> currentHabitChecks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupViewModels();

        // Adapter oluştururken artık taskViewModel parametre olarak ekleniyor
        eventAdapter = new EventAdapter(eventList, taskViewModel, habitCheckViewModel, getViewLifecycleOwner(), selectedDate);
        recyclerView.setAdapter(eventAdapter);

        setupAddButton(view);
        setupEventListeners();

        return view;
    }

    private void setupViewModels() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);
        habitCheckViewModel = new ViewModelProvider(this).get(HabitCheckViewModel.class);

        taskViewModel.getTasksByDate(selectedDate).observe(getViewLifecycleOwner(), tasks -> {
            currentTasks = tasks != null ? tasks : new ArrayList<>();
            updateEventList();
        });

        habitViewModel.getAllHabits().observe(getViewLifecycleOwner(), habits -> {
            currentHabits = habits != null ? habits : new ArrayList<>();

            habitCheckViewModel.getHabitChecksByDate(selectedDate).observe(getViewLifecycleOwner(), checks -> {
                currentHabitChecks = checks != null ? checks : new ArrayList<>();
                updateEventList();
            });
        });
    }

    private void setupAddButton(View view) {
        View icAdd = view.findViewById(R.id.ic_add);
        icAdd.setOnClickListener(v -> {
            AddSelectionBottomSheet bottomSheet = new AddSelectionBottomSheet();
            bottomSheet.setOnSelectionListener(new AddSelectionBottomSheet.OnSelectionListener() {
                @Override
                public void onAddTaskSelected() {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new NewTaskFragment())
                            .addToBackStack(null)
                            .commit();
                }

                @Override
                public void onAddHabitSelected() {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new NewHabitFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });
            bottomSheet.show(getParentFragmentManager(), "AddSelectionBottomSheet");
        });
    }

    private void setupEventListeners() {
        eventAdapter.setOnItemClickListener(item -> {
            if (item.getType() == EventItem.TYPE_TASK) {
                new EditTaskBottomSheetFragment(item.getTask())
                        .show(getParentFragmentManager(), "EditTaskBottomSheet");
            } else if (item.getType() == EventItem.TYPE_HABIT) {
                new EditHabitBottomSheetFragment(item.getHabit())
                        .show(getParentFragmentManager(), "EditHabitBottomSheet");
            }
        });
    }

    private void updateEventList() {
        eventList.clear();

        for (TaskEntity task : currentTasks) {
            eventList.add(new EventItem(task, task.isChecked()));
        }

        for (HabitEntity habit : currentHabits) {
            if (habit.isScheduledForDate(selectedDate)) {
                boolean checked = false;
                for (HabitCheckEntity check : currentHabitChecks) {
                    if (check.getHabitId() == habit.getId()) {
                        checked = check.isChecked();
                        break;
                    }
                }
                eventList.add(new EventItem(habit, checked));
            }
        }

        // Sırala ve bildir
        Collections.sort(eventList, (e1, e2) -> {
            String time1 = e1.getType() == EventItem.TYPE_TASK ? e1.getTask().getTime() : e1.getHabit().getTime();
            String time2 = e2.getType() == EventItem.TYPE_TASK ? e2.getTask().getTime() : e2.getHabit().getTime();
            return compareTimeStrings(time1, time2);
        });

        eventAdapter.setEventList(eventList);
        eventAdapter.setSelectedDate(selectedDate);
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private int compareTimeStrings(String t1, String t2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf.parse(t1).compareTo(sdf.parse(t2));
        } catch (Exception e) {
            return t1.compareTo(t2);
        }
    }
}
