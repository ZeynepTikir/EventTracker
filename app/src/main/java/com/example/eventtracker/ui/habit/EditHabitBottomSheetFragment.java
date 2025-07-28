package com.example.eventtracker.ui.habit;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventtracker.R;
import com.example.eventtracker.data.model.HabitEntity;
import com.example.eventtracker.viewmodel.HabitViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

import androidx.lifecycle.ViewModelProvider;

public class EditHabitBottomSheetFragment extends BottomSheetDialogFragment {

    private HabitEntity habit;
    private HabitViewModel habitViewModel;

    private EditText editHabitName, editHabitTime;
    private TextView deleteText;
    private CheckBox[] dayCheckboxes = new CheckBox[7];
    private final int[] checkboxIds = {
            R.id.checkboxMonday, R.id.checkboxTuesday, R.id.checkboxWednesday,
            R.id.checkboxThursday, R.id.checkboxFriday, R.id.checkboxSaturday, R.id.checkboxSunday
    };

    public EditHabitBottomSheetFragment(HabitEntity habit) {
        this.habit = habit;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_edit_habit, container, false);

        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);

        editHabitName = view.findViewById(R.id.editHabitName);
        editHabitTime = view.findViewById(R.id.editHabitTime);

        // Gün checkbox'ları bağla
        for (int i = 0; i < 7; i++) {
            dayCheckboxes[i] = view.findViewById(checkboxIds[i]);
        }

        // Saat seçici
        editHabitTime.setOnClickListener(v -> showTimePicker());

        // Alanları doldur
        if (habit != null) {
            editHabitName.setText(habit.getName());
            editHabitTime.setText(habit.getTime());

            boolean[] days = habit.getDays();
            if (days != null && days.length == 7) {
                for (int i = 0; i < 7; i++) {
                    dayCheckboxes[i].setChecked(days[i]);
                }
            }
        }

        // Güncelle butonu
        view.findViewById(R.id.updateButton).setOnClickListener(v -> {
            String name = editHabitName.getText().toString().trim();
            String time = editHabitTime.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(time)) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean[] days = new boolean[7];
            for (int i = 0; i < 7; i++) {
                days[i] = dayCheckboxes[i].isChecked();
            }

            habit.setName(name);
            habit.setTime(time);
            habit.setDays(days);

            habitViewModel.update(habit);
            dismiss();
        });

        // Sil butonu
        deleteText = view.findViewById(R.id.deleteText);
        deleteText.setText("Delete");
        deleteText.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Alışkanlığı Sil")
                    .setMessage("Bu alışkanlığı silmek istediğinize emin misiniz?")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        habitViewModel.delete(habit);
                        dismiss();
                    })
                    .setNegativeButton("İptal", null)
                    .show();
        });


        return view;
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(getContext(), (TimePicker view, int hourOfDay, int minute1) -> {
            String time = String.format("%02d:%02d", hourOfDay, minute1);
            editHabitTime.setText(time);
        }, hour, minute, true).show();
    }
}
