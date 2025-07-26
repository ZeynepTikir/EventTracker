package com.example.eventtracker.ui.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventtracker.R;
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.viewmodel.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewTaskFragment extends Fragment {

    private EditText taskNameEditText, taskDateEditText, taskTimeEditText;
    private TaskViewModel taskViewModel;
    private final Calendar calendar = Calendar.getInstance();

    public NewTaskFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_task, container, false);

        taskNameEditText = view.findViewById(R.id.taskNameEditText);
        taskDateEditText = view.findViewById(R.id.taskDateEditText);
        taskTimeEditText = view.findViewById(R.id.taskTimeEditText);
        Button addButton = view.findViewById(R.id.addButton);
        TextView cancelText = view.findViewById(R.id.cancelText);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        //date and time
        taskDateEditText.setOnClickListener(v -> showDatePicker());
        taskTimeEditText.setOnClickListener(v -> showTimePicker());


        //add
        addButton.setOnClickListener(v -> {
            String name = taskNameEditText.getText().toString().trim();
            String date = taskDateEditText.getText().toString().trim();
            String time = taskTimeEditText.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            TaskEntity task = new TaskEntity();
            task.setName(name);
            task.setDate(date);
            task.setTime(time);
            task.setIcon("ic_task"); // varsayılan bir icon adı
            task.setChecker(false);  // başlangıçta işaretli değil

            taskViewModel.insert(task);

            Toast.makeText(getContext(), "Task Added", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        //cancel
        cancelText.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void showDatePicker() {
        new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(calendar.getTime());
                    taskDateEditText.setText(formattedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void showTimePicker() {
        new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    taskTimeEditText.setText(formattedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true)
                .show();
    }
}
