package com.example.eventtracker.ui.navfragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.eventtracker.R;
import com.example.eventtracker.data.model.PomodoroEntity;
import com.example.eventtracker.data.model.PomodoroWithTaskName;
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.data.AppDatabase;
import com.example.eventtracker.viewmodel.TaskViewModel;

import java.util.List;

public class PomodoroFragment extends Fragment {

    private static final String TAG = "PomodoroFragment";

    private Spinner taskSpinner;
    private TextView timerText;
    private Button startBtn, pauseBtn, resetBtn;
    private CardView taskCardView;

    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private long timeLeftInMillis;
    private long selectedDuration;

    private TaskViewModel taskViewModel;
    private List<TaskEntity> taskList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        taskSpinner = view.findViewById(R.id.taskSpinner);
        timerText = view.findViewById(R.id.timerText);
        startBtn = view.findViewById(R.id.startBtn);
        pauseBtn = view.findViewById(R.id.pauseBtn);
        resetBtn = view.findViewById(R.id.resetBtn);
        taskCardView = view.findViewById(R.id.taskCardView);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        applyThemeColors(view);

        setupTaskSpinner();
        loadDurationFromSettings();

        timeLeftInMillis = selectedDuration;
        updateTimerText();

        startBtn.setOnClickListener(v -> {
            if (isRunning) stopTimer();
            else startTimer();
        });

        pauseBtn.setOnClickListener(v -> pauseTimer());
        resetBtn.setOnClickListener(v -> resetTimer());

        // 🔽 BURAYA DB LOG KOMUTU EKLİYORUZ
        // DB log kısmı
        new Thread(() -> {
            List<PomodoroWithTaskName> pomodoros = AppDatabase
                    .getInstance(requireContext())
                    .pomodoroDao()
                    .getRecentPomodorosWithTaskName(); // POJO kullanılıyor

            for (PomodoroWithTaskName p : pomodoros) {
                Log.d(TAG, "Pomodoro: taskId=" + p.getTaskId()
                        + ", name=" + p.getTaskName()
                        + ", duration=" + p.getDuration()
                        + ", completed=" + p.isCompleted()
                        + ", timestamp=" + p.getTimestamp());
            }
        }).start();


        //Log.d(TAG, "PomodoroFragment initialized with duration=" + selectedDuration + "ms");
        return view;
    }

    private void setupTaskSpinner() {
        taskViewModel.getActiveUncheckedTasks().observe(getViewLifecycleOwner(), tasks -> {
            taskList = tasks;
            if (tasks != null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
                boolean darkMode = prefs.getBoolean("dark_mode", false);
                int textColor = darkMode
                        ? getResources().getColor(R.color.dark_textcolor, null)
                        : getResources().getColor(R.color.textcolor, null);

                ArrayAdapter<TaskEntity> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        tasks
                ) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        TextView label = (TextView) super.getView(position, convertView, parent);
                        label.setText(getItem(position).getName());
                        label.setTextColor(textColor); // burada tema rengi uygulanıyor
                        return label;
                    }

                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
                        label.setText(getItem(position).getName());
                        label.setTextColor(textColor); // yazı rengi

                        // Tema'ya göre arka plan rengi
                        int bgColor = darkMode
                                ? getResources().getColor(R.color.dark_cardBackground, null)
                                : getResources().getColor(R.color.cardBackground, null);

                        label.setBackgroundColor(bgColor);

                        return label;
                    }

                };
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                taskSpinner.setAdapter(adapter);
                //Log.d(TAG, "Task spinner loaded with " + tasks.size() + " tasks");
            }
        });
    }


    private void loadDurationFromSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        int minutes = prefs.getInt("pomodoro_duration", 25); // default 25dk
        selectedDuration = minutes * 60 * 1000L;
        // Log.d(TAG, "Loaded Pomodoro duration from settings: " + minutes + " min");
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                savePomodoro(true);
                timeLeftInMillis = selectedDuration;
                updateTimerText();
                startBtn.setText(R.string.start);
                //Log.d(TAG, "Pomodoro finished!");
            }
        }.start();

        isRunning = true;
        startBtn.setText(R.string.stop);
        //Log.d(TAG, "Pomodoro started");
    }

    private void stopTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        savePomodoro(false);
        timeLeftInMillis = selectedDuration;
        updateTimerText();
        isRunning = false;
        startBtn.setText(R.string.start);
        // Log.d(TAG, "Pomodoro stopped by user");
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isRunning = false;
            // Log.d(TAG, "Pomodoro paused at " + (timeLeftInMillis / 1000) + "s left");
        }
    }

    private void resetTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        timeLeftInMillis = selectedDuration;
        updateTimerText();
        isRunning = false;
        startBtn.setText(R.string.start);
        // Log.d(TAG, "Pomodoro reset");
    }

    private void savePomodoro(boolean completed) {
        TaskEntity selectedTask = (TaskEntity) taskSpinner.getSelectedItem();
        if (selectedTask == null) {
            // Log.d(TAG, "No task selected, Pomodoro not saved");
            return;
        }

        long duration = selectedDuration - timeLeftInMillis;
        long timestamp = System.currentTimeMillis();

        // PomodoroEntity artık sadece taskId ile ilişkili
        PomodoroEntity pomodoro = new PomodoroEntity(
                selectedTask.getId(), // taskId
                duration,
                completed,
                timestamp
        );

        new Thread(() -> {
            AppDatabase.getInstance(requireContext())
                    .pomodoroDao()
                    .insert(pomodoro);
            Log.d(TAG, "Pomodoro saved: taskId=" + selectedTask.getId()
                    + ", duration=" + duration + "ms, completed=" + completed);
        }).start();
    }


    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        timerText.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void applyThemeColors(View root) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean darkMode = prefs.getBoolean("dark_mode", false);

        int bgColor = darkMode
                ? getResources().getColor(R.color.dark_background, null)
                : getResources().getColor(R.color.background, null);

        int cardColor = darkMode
                ? getResources().getColor(R.color.dark_cardBackground, null)
                : getResources().getColor(R.color.cardBackground, null);

        int textColor = darkMode
                ? getResources().getColor(R.color.dark_textcolor, null)
                : getResources().getColor(R.color.textcolor, null);

        int buttonColor = darkMode
                ? getResources().getColor(R.color.dark_button, null)
                : getResources().getColor(R.color.button, null);

        int buttonTextColor = darkMode
                ? getResources().getColor(R.color.dark_button_text, null)
                : getResources().getColor(R.color.buttonText, null);

        root.setBackgroundColor(bgColor);

        // TextView ve Spinner renklerini ayarla
        setTextColorsRecursively(root, textColor);

        // Cardview arkaplanı
        taskCardView.setCardBackgroundColor(cardColor);

        // FrameLayout içindeki View rengi
        View circleView = root.findViewById(R.id.circleView);
        Drawable drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_circle);
        drawable.setTint(darkMode ? textColor : buttonColor);
        circleView.setBackground(drawable);

        // Buton renkleri
        startBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(buttonColor));
        startBtn.setTextColor(buttonTextColor);

        pauseBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(buttonColor));
        pauseBtn.setTextColor(buttonTextColor);

        resetBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(buttonColor));
        resetBtn.setTextColor(buttonTextColor);
    }

    private void setTextColorsRecursively(View view, int textColor) {
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(textColor);
        } else if (view instanceof Spinner) {
            // Spinner text renklerini de ayarlayabiliriz (basit adapter kullanıyorsak)
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                setTextColorsRecursively(group.getChildAt(i), textColor);
            }
        }
    }
}
