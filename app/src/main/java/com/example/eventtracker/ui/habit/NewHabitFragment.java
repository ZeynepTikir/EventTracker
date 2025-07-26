package com.example.eventtracker.ui.habit;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.eventtracker.R;
import com.example.eventtracker.data.model.HabitEntity;
import com.example.eventtracker.viewmodel.HabitViewModel;
import java.util.Calendar;

public class NewHabitFragment extends Fragment {

    private EditText habitNameEditText;
    private TextView habitTimeTextView, cancelText;
    private Button saveButton;
    private CheckBox[] dayCheckboxes = new CheckBox[7]; // Pazartesi = 0, Pazar = 6
    private HabitViewModel habitViewModel;

    private String selectedTime = "";

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_habit, container, false);

        habitNameEditText = view.findViewById(R.id.editHabitName);
        habitTimeTextView = view.findViewById(R.id.editHabitTime);
        saveButton = view.findViewById(R.id.addButton);
        cancelText = view.findViewById(R.id.cancelText);



        dayCheckboxes[0] = view.findViewById(R.id.checkboxMonday);
        dayCheckboxes[1] = view.findViewById(R.id.checkboxTuesday);
        dayCheckboxes[2] = view.findViewById(R.id.checkboxWednesday);
        dayCheckboxes[3] = view.findViewById(R.id.checkboxThursday);
        dayCheckboxes[4] = view.findViewById(R.id.checkboxFriday);
        dayCheckboxes[5] = view.findViewById(R.id.checkboxSaturday);
        dayCheckboxes[6] = view.findViewById(R.id.checkboxSunday);

        for (CheckBox checkBox : dayCheckboxes) {
            checkBox.setButtonDrawable(null); // Tik kutusunu kaldırır
            //checkBox.setVisibility(View.VISIBLE); // TextView gibi görünmesini sağlar
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Context context = view.getContext();
                if (isChecked) {
                    checkBox.setBackgroundColor(ContextCompat.getColor(context, R.color.button));
                    checkBox.setTextColor(ContextCompat.getColor(context, R.color.white));
                    checkBox.setBackgroundResource(R.drawable.day_checkbox_background);
                } else {
                    checkBox.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    checkBox.setTextColor(ContextCompat.getColor(context, R.color.textcolor));
                    checkBox.setBackgroundResource(R.drawable.day_checkbox_background);
                }

            });

        }




        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);

        habitTimeTextView.setOnClickListener(v -> showTimePicker());

        saveButton.setOnClickListener(v -> saveHabit());

        cancelText.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(requireContext(), (view, hourOfDay, minute1) -> {
            selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
            habitTimeTextView.setText(selectedTime);
        }, hour, minute, true).show();
    }

    private void saveHabit() {
        String name = habitNameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            habitNameEditText.setError("Lütfen bir alışkanlık adı girin");
            return;
        }

        if (TextUtils.isEmpty(selectedTime)) {
            Toast.makeText(getContext(), "Lütfen bir saat seçin", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean[] selectedDays = new boolean[7];
        boolean atLeastOneDaySelected = false;
        StringBuilder daysLog = new StringBuilder("Selected days: ");

        for (int i = 0; i < 7; i++) {
            selectedDays[i] = dayCheckboxes[i].isChecked();
            if (selectedDays[i]) {
                atLeastOneDaySelected = true;
            }
            daysLog.append(selectedDays[i] ? "1" : "0");
            if (i < 6) daysLog.append(",");
        }

        android.util.Log.d("NewHabitFragment", daysLog.toString());

        if (!atLeastOneDaySelected) {
            Toast.makeText(getContext(), "Lütfen en az bir gün seçiniz", Toast.LENGTH_SHORT).show();
            return;
        }

        HabitEntity habit = new HabitEntity();
        habit.setName(name);
        habit.setTime(selectedTime);
        habit.setIcon("ic_habit");
        habit.setChecker(false);
        habit.setDays(selectedDays);

        habitViewModel.insert(habit);
        Toast.makeText(getContext(), "Alışkanlık kaydedildi", Toast.LENGTH_SHORT).show();
        clearFields();
        requireActivity().getSupportFragmentManager().popBackStack();
    }



    private void clearFields() {
        habitNameEditText.setText("");
        habitTimeTextView.setText("Saat seç");
        selectedTime = "";
        for (CheckBox cb : dayCheckboxes) cb.setChecked(false);
    }

}
