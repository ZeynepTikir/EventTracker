package com.example.eventtracker;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.eventtracker.ui.CalendarFragment;
import com.example.eventtracker.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Varsayılan fragment
        loadFragment(new HomeFragment());

        // Seçim dinleyici
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                selected = new HomeFragment();
            } else if (id == R.id.navigation_calendar) {
                selected = new CalendarFragment();
            }
            /*
            else if (id == R.id.navigation_pomodoro) {
                selected = new PomodoroFragment();
            } else if (id == R.id.navigation_habits) {
                selected = new HabitFragment();
            } else if (id == R.id.navigation_settings) {
                selected = new SettingsFragment();
            }
             */

            if (selected != null) {
                loadFragment(selected);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
