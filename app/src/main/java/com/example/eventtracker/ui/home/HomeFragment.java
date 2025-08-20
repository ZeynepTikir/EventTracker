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
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.ui.adapter.EventAdapter;
import com.example.eventtracker.ui.task.EditTaskBottomSheetFragment;
import com.example.eventtracker.ui.task.NewTaskFragment;
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
    private final List<TaskEntity> taskList = new ArrayList<>();

    private TaskViewModel taskViewModel;

    private final String selectedDate = getTodayDate();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupViewModels();

        // Adapter oluşturuluyor
        eventAdapter = new EventAdapter(taskList, taskViewModel);
        recyclerView.setAdapter(eventAdapter);

        setupAddButton(view);
        setupEventListeners();

        return view;
    }

    private void setupViewModels() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Bugünün görevlerini canlı olarak gözlemle
        taskViewModel.getTasksByDate(selectedDate).observe(getViewLifecycleOwner(), tasks -> {
            updateTaskList(tasks != null ? tasks : new ArrayList<>());
        });
    }

    private void setupAddButton(View view) {
        View icAdd = view.findViewById(R.id.ic_add);
        if (icAdd != null) {
            icAdd.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new NewTaskFragment())
                    .addToBackStack(null)
                    .commit());
        } else {
            Log.e("HomeFragment", "ic_add button not found in layout");
        }
    }

    private void setupEventListeners() {
        eventAdapter.setOnItemClickListener(task -> {
            new EditTaskBottomSheetFragment(task)
                    .show(getParentFragmentManager(), "EditTaskBottomSheet");
        });
    }

    private void updateTaskList(List<TaskEntity> tasks) {
        taskList.clear();
        taskList.addAll(tasks);

        // Görevleri saate göre sırala
        Collections.sort(taskList, (t1, t2) -> compareTimeStrings(t1.getTime(), t2.getTime()));

        eventAdapter.setTaskList(taskList);
    }

    private static String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private int compareTimeStrings(String t1, String t2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date d1 = sdf.parse(t1);
            Date d2 = sdf.parse(t2);
            if (d1 != null && d2 != null) return d1.compareTo(d2);
        } catch (Exception e) {
            Log.e("HomeFragment", "Time parsing error: " + e.getMessage());
        }
        return t1.compareTo(t2);
    }
}
