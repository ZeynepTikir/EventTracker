package com.example.eventtracker;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.eventtracker.ui.CalendarFragment;
import com.example.eventtracker.ui.home.HomeFragment;
import com.example.eventtracker.ui.task.NewTaskFragment;
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

        //Navigasyon görünürlüğü
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            View viewShadow = findViewById(R.id.viewShadow);

            boolean shouldHideNav = currentFragment instanceof NewTaskFragment;

            if (shouldHideNav) {
                bottomNav.setVisibility(View.GONE);
                viewShadow.setVisibility(View.GONE);  // Gölgeyi de gizle
            } else {
                bottomNav.setVisibility(View.VISIBLE);
                viewShadow.setVisibility(View.VISIBLE);  // Geri getir
            }
        });


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

