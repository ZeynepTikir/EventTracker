package com.example.eventtracker.ui.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.eventtracker.R;
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.utils.AlarmHelper;
import com.example.eventtracker.viewmodel.TaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditTaskBottomSheetFragment extends BottomSheetDialogFragment
        implements IconPickerDialog.OnIconSelectedListener {

    private EditText taskNameEditText, taskDateEditText, taskTimeEditText;
    private ImageView imageViewIcon;
    private TextView deleteText, title;
    private Button updateButton;
    private final Calendar calendar = Calendar.getInstance();

    private TaskViewModel taskViewModel;
    private TaskEntity task;

    private int selectedIconResId = R.drawable.ic_task; // varsayılan icon

    public EditTaskBottomSheetFragment(TaskEntity task) {
        this.task = task;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_task, container, false);

        title = view.findViewById(R.id.title);
        taskNameEditText = view.findViewById(R.id.taskNameEditText);
        taskDateEditText = view.findViewById(R.id.taskDateEditText);
        taskTimeEditText = view.findViewById(R.id.taskTimeEditText);
        imageViewIcon = view.findViewById(R.id.imageViewIcon);
        updateButton = view.findViewById(R.id.addButton);
        deleteText = view.findViewById(R.id.cancelText);

        applyThemeColors(view);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        // Mevcut task bilgilerini doldur
        if (task != null) {
            taskNameEditText.setText(task.getName());
            taskDateEditText.setText(task.getDate());
            taskTimeEditText.setText(task.getTime());

            // Context-safe icon yükleme
            selectedIconResId = getIconResIdFromName(task.getIcon());
            imageViewIcon.setImageResource(selectedIconResId);
        }

        // Icon değişimi
        imageViewIcon.setOnClickListener(v -> {
            IconPickerDialog dialog = new IconPickerDialog();
            dialog.setTargetFragment(this, 0);
            dialog.show(getParentFragmentManager(), "icon_picker");
        });

        // Date & Time pickers
        taskDateEditText.setOnClickListener(v -> showDatePicker());
        taskTimeEditText.setOnClickListener(v -> showTimePicker());

        // Update button
        updateButton.setOnClickListener(v -> {
            String name = taskNameEditText.getText().toString().trim();
            String date = taskDateEditText.getText().toString().trim();
            String time = taskTimeEditText.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
                Toast.makeText(getContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            task.setName(name);
            task.setDate(date);
            task.setTime(time);
            task.setIcon(getResources().getResourceEntryName(selectedIconResId));

            // Veritabanını güncelle
            taskViewModel.update(task);

            // Alarmı güncelle: eski alarm iptal edilir, yeni tarih-saat ile kurulur
            AlarmHelper.updateTaskReminder(requireContext(), task);

            Toast.makeText(getContext(), getString(R.string.toast_task_updated), Toast.LENGTH_SHORT).show();
            dismiss();
        });

// Delete button
        deleteText.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.delete_task))
                    .setMessage(getString(R.string.delete_task_confirm))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        // Alarmı iptal et
                        AlarmHelper.cancelTaskReminder(requireContext(), task);

                        // Görevi sil
                        taskViewModel.delete(task);
                        dismiss();
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });



        return view;
    }

    private void showDatePicker() {
        new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String formattedDate = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault())
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
                    String formattedTime = String.format(Locale.getDefault(), getString(R.string.time_format), hourOfDay, minute);
                    taskTimeEditText.setText(formattedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true)
                .show();
    }

    private int getIconResIdFromName(String iconName) {
        if (iconName == null || iconName.isEmpty()) return R.drawable.ic_task;
        return getResources().getIdentifier(iconName, "drawable", requireContext().getPackageName());
    }

    @Override
    public void onIconSelected(int iconResId) {
        selectedIconResId = iconResId;
        imageViewIcon.setImageResource(iconResId);
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
        int buttonColor = ContextCompat.getColor(requireContext(),
                darkMode ? R.color.dark_button : R.color.button);
        int buttonTextColor = ContextCompat.getColor(requireContext(),
                darkMode ? R.color.dark_button_text : R.color.buttonText);
        int cancelTextColor = ContextCompat.getColor(requireContext(),
                darkMode ? R.color.dark_cancelColor : R.color.cancelColor);

        // Sayfa ve card arkaplanı
        rootView.setBackgroundColor(backgroundColor);

        // Text ve EditText renkleri
        title.setTextColor(textColor);
        taskNameEditText.setTextColor(textColor);
        taskDateEditText.setTextColor(textColor);
        taskTimeEditText.setTextColor(textColor);

        taskNameEditText.setHintTextColor(textColor);
        taskDateEditText.setHintTextColor(textColor);
        taskTimeEditText.setHintTextColor(textColor);

        // EditText arka planını dinamik değiştirme
        if (taskNameEditText.getBackground() instanceof GradientDrawable) {
            GradientDrawable bg = (GradientDrawable) taskNameEditText.getBackground();
            bg.setColor(cardColor);
        }
        if (taskDateEditText.getBackground() instanceof GradientDrawable) {
            GradientDrawable bg = (GradientDrawable) taskDateEditText.getBackground();
            bg.setColor(cardColor);
        }
        if (taskTimeEditText.getBackground() instanceof GradientDrawable) {
            GradientDrawable bg = (GradientDrawable) taskTimeEditText.getBackground();
            bg.setColor(cardColor);
        }

        // Buton
        updateButton.setBackgroundTintList(ColorStateList.valueOf(buttonColor));
        updateButton.setTextColor(ColorStateList.valueOf(buttonTextColor));

        // Cancel Text rengi
        deleteText.setTextColor(cancelTextColor);
    }
}
