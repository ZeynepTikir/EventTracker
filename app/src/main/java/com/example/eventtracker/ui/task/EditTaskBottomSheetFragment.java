package com.example.eventtracker.ui.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditTaskBottomSheetFragment extends BottomSheetDialogFragment {

    private EditText taskNameEditText, taskDateEditText, taskTimeEditText;
    private TextView deleteText;
    private Button updateButton;
    private final Calendar calendar = Calendar.getInstance();

    private TaskViewModel taskViewModel;

    private TaskEntity task; // düzenlenen task

    public EditTaskBottomSheetFragment(TaskEntity task) {
        this.task = task;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_edit_task, container, false);

        taskNameEditText = view.findViewById(R.id.taskNameEditText);
        taskDateEditText = view.findViewById(R.id.taskDateEditText);
        taskTimeEditText = view.findViewById(R.id.taskTimeEditText);
        deleteText = view.findViewById(R.id.deleteText);
        updateButton = view.findViewById(R.id.updateButton);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        // Mevcut bilgileri doldur
        if (task != null) {
            taskNameEditText.setText(task.getName());
            taskDateEditText.setText(task.getDate());
            taskTimeEditText.setText(task.getTime());
        }

        // Tarih ve saat seçimleri için datepicker/timepicker ekleyebilirsin, basit örnek:
        taskDateEditText.setOnClickListener(v -> showDatePicker());
        taskTimeEditText.setOnClickListener(v -> showTimePicker());

        updateButton.setOnClickListener(v -> {
            String name = taskNameEditText.getText().toString().trim();
            String date = taskDateEditText.getText().toString().trim();
            String time = taskTimeEditText.getText().toString().trim();

            if (name.isEmpty()) {
                taskNameEditText.setError("Task name required");
                return;
            }

            // task nesnesini güncelle
            task.setName(name);
            task.setDate(date);
            task.setTime(time);

            taskViewModel.update(task);
            dismiss();
        });

        deleteText.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (task != null) {
                            taskViewModel.delete(task);
                            dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


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
