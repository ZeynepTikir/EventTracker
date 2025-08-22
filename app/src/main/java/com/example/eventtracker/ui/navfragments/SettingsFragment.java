package com.example.eventtracker.ui.navfragments;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.eventtracker.MainActivity;
import com.example.eventtracker.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private RadioGroup themeRadioGroup;
    private RadioButton radioLight, radioDark;
    private Spinner languageSpinner;
    private SwitchMaterial notificationSwitch;
    private Button clearDataButton;
    private TextView settingsTitle;

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Viewleri bağla
        themeRadioGroup = view.findViewById(R.id.themeRadioGroup);
        radioLight = view.findViewById(R.id.radioLight);
        radioDark = view.findViewById(R.id.radioDark);
        languageSpinner = view.findViewById(R.id.languageSpinner);
        notificationSwitch = view.findViewById(R.id.notificationSwitch);
        clearDataButton = view.findViewById(R.id.clearDataButton);
        settingsTitle = view.findViewById(R.id.settingsTitle);

        // Önceki ayarları yükle
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        Bundle args = getArguments();
        if (args != null) {
            isDarkMode = args.getBoolean("isDarkMode", false);
        }
        if (isDarkMode) {
            radioDark.setChecked(true);
        } else {
            radioLight.setChecked(true);
        }

        // Renkleri uygula
        applyThemeColors(view, isDarkMode);

        boolean notifications = sharedPreferences.getBoolean("notifications", true);
        notificationSwitch.setChecked(notifications);

        // Tema seçimi dinleyici
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean darkModeSelected = checkedId == R.id.radioDark;

            // Preference kaydet
            sharedPreferences.edit().putBoolean("dark_mode", darkModeSelected).apply();

            // Renkleri uygula
            applyThemeColors(view, darkModeSelected);

            // MainActivity'yi yeniden başlatarak tüm renkleri uygula
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).refreshTheme(darkModeSelected);
            }

        });


        // Bildirim listener
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean("notifications", isChecked).apply()
        );

        // Dil spinner
        String[] languages = {"English", "Turkish", "Spanish"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Clear data butonu
        clearDataButton.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Clear App Data")
                    .setMessage("Are you sure you want to clear all app data? This action cannot be undone.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // SharedPreferences temizleme
                        sharedPreferences.edit().clear().apply();
                        Toast.makeText(requireContext(), "App data cleared", Toast.LENGTH_SHORT).show();

                        // Settings fragment’i yeniden yükle
                        requireActivity().recreate();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        return view;
    }

    public void applyThemeColors(View root, boolean darkMode) {
        int bgColor = darkMode
                ? getResources().getColor(R.color.dark_background, null)
                : getResources().getColor(R.color.background, null);

        int textColor = darkMode
                ? getResources().getColor(R.color.dark_textcolor, null)
                : getResources().getColor(R.color.textcolor, null);

        int buttonColor = darkMode
                ? getResources().getColor(R.color.dark_button, null)
                : getResources().getColor(R.color.button, null);

        int buttonTextColor = darkMode
                ? getResources().getColor(R.color.dark_button_text, null)
                : getResources().getColor(R.color.buttonText, null);

        // Arka plan
        root.setBackgroundColor(bgColor);

        // Tüm text tabanlı view'lerin rengini ayarla
        setTextColorsRecursively(root, textColor);

        // Sadece buton özel: arka plan + text rengi
        clearDataButton.setBackgroundTintList(ColorStateList.valueOf(buttonColor));
        clearDataButton.setTextColor(buttonTextColor);
    }

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



}
