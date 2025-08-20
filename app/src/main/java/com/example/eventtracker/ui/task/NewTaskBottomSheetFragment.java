package com.example.eventtracker.ui.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventtracker.R;
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.viewmodel.TaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewTaskBottomSheetFragment extends BottomSheetDialogFragment
        implements IconPickerDialog.OnIconSelectedListener {

    private EditText taskNameEditText, taskDateEditText, taskTimeEditText;
    private ImageView imageViewIcon;
    private TaskViewModel taskViewModel;
    private final Calendar calendar = Calendar.getInstance();
    private int selectedIconResId = R.drawable.ic_task; // varsayılan icon

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_task, container, false);

        taskNameEditText = view.findViewById(R.id.taskNameEditText);
        taskDateEditText = view.findViewById(R.id.taskDateEditText);
        taskTimeEditText = view.findViewById(R.id.taskTimeEditText);
        imageViewIcon = view.findViewById(R.id.imageViewIcon);
        Button addButton = view.findViewById(R.id.addButton);
        TextView cancelText = view.findViewById(R.id.cancelText);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        // Icon değişimi
        imageViewIcon.setOnClickListener(v -> {
            IconPickerDialog dialog = new IconPickerDialog();
            dialog.setTargetFragment(this, 0);
            dialog.show(getParentFragmentManager(), "icon_picker");
        });

        // Date ve Time picker
        taskDateEditText.setOnClickListener(v -> showDatePicker());
        taskTimeEditText.setOnClickListener(v -> showTimePicker());

        // Add
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
            task.setIcon(getResources().getResourceEntryName(selectedIconResId));
            task.setChecked(false);

            taskViewModel.insert(task);

            Toast.makeText(getContext(), "Task Added", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        // Cancel
        cancelText.setOnClickListener(v -> dismiss());

        // Başlangıç iconu
        imageViewIcon.setImageResource(selectedIconResId);

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

    @Override
    public void onIconSelected(int iconResId) {
        Log.d("IconPickerDialog", "Selected icon: " + iconResId);
        selectedIconResId = iconResId;
        imageViewIcon.setImageResource(iconResId);
    }
}
