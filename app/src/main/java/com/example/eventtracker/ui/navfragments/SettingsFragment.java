package com.example.eventtracker.ui.navfragments;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
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
import com.example.eventtracker.utils.LocaleHelper;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private RadioGroup themeRadioGroup;
    private RadioButton radioLight, radioDark;
    private Spinner languageSpinner, durationSpinner;
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
        durationSpinner = view.findViewById(R.id.durationSpinner);

        // Önceki tema ayarını yükle
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        if (isDarkMode) radioDark.setChecked(true);
        else radioLight.setChecked(true);

        // Tema renklerini uygula
        applyThemeColors(view, isDarkMode);

        // Bildirim ayarını yükle
        boolean notifications = sharedPreferences.getBoolean("notifications", true);
        notificationSwitch.setChecked(notifications);

        // Tema değişikliği dinleyici
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean darkModeSelected = checkedId == R.id.radioDark;
            sharedPreferences.edit().putBoolean("dark_mode", darkModeSelected).apply();
            applyThemeColors(view, darkModeSelected);
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).refreshTheme(darkModeSelected);
            }
        });

        // Bildirim listener
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean("notifications", isChecked).apply()
        );

        // Dil spinner
        String[] languages = {"English", "Türkçe", "Español"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Önceki dili yükle ve spinner'ı ayarla
        String savedLanguage = sharedPreferences.getString("language", "en");
        int spinnerPosition = 0;
        switch (savedLanguage) {
            case "tr": spinnerPosition = 1; break;
            case "es": spinnerPosition = 2; break;
        }
        languageSpinner.setSelection(spinnerPosition);

        // Dil değişikliği listener
        languageSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String langCode = "en";
                switch (position) {
                    case 1: langCode = "tr"; break;
                    case 2: langCode = "es"; break;
                }
                String currentLang = sharedPreferences.getString("language", "en");
                if (!currentLang.equals(langCode)) {
                    sharedPreferences.edit().putString("language", langCode).apply();
                    updateLocale(langCode);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Pomodoro Süre seçeneklerini tanımla (dakika cinsinden)
        String[] durations = {"15 min", "20 min", "25 min", "30 min", "45 min", "60 min"};
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, durations);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(durationAdapter);

        // Pomodoro Süre ayarını yükle
        int savedDuration = sharedPreferences.getInt("pomodoro_duration", 25); // default 25 dk
        int spinnerIndex = 2; // default olarak 25 min seçili
        for (int i = 0; i < durations.length; i++) {
            if (durations[i].startsWith(String.valueOf(savedDuration))) {
                spinnerIndex = i;
                break;
            }
        }
        durationSpinner.setSelection(spinnerIndex);

        // Pomodoro Süre Spinner seçimi dinleyici
        durationSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = durations[position];
                int minutes = Integer.parseInt(selected.split(" ")[0]); // "25 min" → 25
                sharedPreferences.edit().putInt("pomodoro_duration", minutes).apply();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Clear data butonu
        clearDataButton.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle(R.string.clear_app_data)
                    .setMessage(R.string.clear_app_data_message)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        sharedPreferences.edit().clear().apply();
                        Toast.makeText(requireContext(), R.string.app_data_cleared, Toast.LENGTH_SHORT).show();
                        requireActivity().recreate();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .show();
        });

        return view;
    }

    // Locale güncelleme
    private void updateLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());

        // LocaleHelper kullanarak kaydet ve güncelle
        LocaleHelper.setLocale(requireContext(), langCode);

        // Activity yeniden başlat
        requireActivity().recreate();
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

        root.setBackgroundColor(bgColor);
        setTextColorsRecursively(root, textColor);

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
