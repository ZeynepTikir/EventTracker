package com.example.eventtracker;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.eventtracker.ui.CalendarFragment;
import com.example.eventtracker.ui.navfragments.HomeFragment;
import com.example.eventtracker.ui.navfragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Tema uygula
        boolean darkMode = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("dark_mode", false);
        applyThemeColors(findViewById(android.R.id.content), darkMode);

        // Varsayılan fragment
        int lastTab = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt("last_selected_tab", R.id.navigation_home);
        bottomNavigationView.setSelectedItemId(lastTab);

        loadFragmentById(lastTab);

        // Seçim dinleyici
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putInt("last_selected_tab", id)
                    .apply();

            loadFragmentById(id);
            return true;
        });
    }

    private void loadFragmentById(int id) {
        Fragment selected = null;
        if (id == R.id.navigation_home) {
            selected = new HomeFragment();
        } else if (id == R.id.navigation_calendar) {
            selected = new CalendarFragment();
        } else if (id == R.id.navigation_settings) {
            selected = new SettingsFragment();
        }
        if (selected != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selected)
                    .commit();
        }
    }

    public void refreshTheme(boolean darkMode) {
        applyThemeColors(findViewById(android.R.id.content), darkMode);

        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (current instanceof SettingsFragment) {
            ((SettingsFragment) current).applyThemeColors(current.getView(), darkMode);
        }
        // Diğer fragment’lar için de benzer şekilde çağırabilirsin
    }



    // 🔽 Tüm textleri recursive boyama fonksiyonu
    private void setTextColorsRecursively(View view, int textColor) {
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(textColor);
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                setTextColorsRecursively(group.getChildAt(i), textColor);
            }
        }
    }

    // 🔽 Tema renklerini uygula
    private void applyThemeColors(View root, boolean darkMode) {
        int bgColor = darkMode
                ? getResources().getColor(R.color.dark_background, null)
                : getResources().getColor(R.color.background, null);

        int cardColor = darkMode
                ? getResources().getColor(R.color.dark_cardBackground, null)
                : getResources().getColor(R.color.cardBackground, null);

        int textColor = darkMode
                ? getResources().getColor(R.color.dark_textcolor, null)
                : getResources().getColor(R.color.textcolor, null);

        int buttonColor = ContextCompat.getColor(this, R.color.button);
        int unselectedColor = ContextCompat.getColor(this,
                darkMode ? R.color.dark_textcolor : R.color.textcolor);


        int statusBarColor = ContextCompat.getColor(this,
                darkMode ? R.color.dark_background : R.color.background);

        // Arka plan
        root.setBackgroundColor(bgColor);

        // Alt navigation
        ColorStateList iconColors = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},   // seçili
                        new int[]{-android.R.attr.state_checked}  // seçili değil
                },
                new int[]{
                        buttonColor,    // seçili renk
                        unselectedColor // seçili değil renk
                }
        );

        bottomNavigationView.setBackgroundTintList(ColorStateList.valueOf(cardColor));
        bottomNavigationView.setItemIconTintList(iconColors);
        bottomNavigationView.setItemTextColor(iconColors);

        // Tüm yazıların rengini değiştir
        setTextColorsRecursively(root, textColor);

        // Status bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(statusBarColor);

        // İkonların rengini ayarla (light / dark)
        View decor = window.getDecorView();
        if (!darkMode) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // koyu ikonlar
        } else {
            decor.setSystemUiVisibility(0); // açık ikonlar
        }
    }
}
