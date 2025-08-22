package com.example.eventtracker.ui.task;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.eventtracker.R;
import com.example.eventtracker.utils.IconUtils;

import java.util.List;

public class IconPickerDialog extends DialogFragment {

    public interface OnIconSelectedListener {
        void onIconSelected(int iconResId);
    }

    private OnIconSelectedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // sadece targetFragment üzerinden al
        if (getTargetFragment() instanceof OnIconSelectedListener) {
            listener = (OnIconSelectedListener) getTargetFragment();
            android.util.Log.d("IconPickerDialog", "Listener set from targetFragment");
        } else {
            android.util.Log.d("IconPickerDialog", "Listener null");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.select_icon);

        // Düzeltme: root layout'u alıyoruz
        final android.view.View dialogView = getLayoutInflater()
                .inflate(R.layout.dialog_icon_picker, null);

        GridView gridView = dialogView.findViewById(R.id.iconGrid);

        List<Integer> icons = IconUtils.getAllIcons(requireContext());
        IconPickerGridAdapter adapter = new IconPickerGridAdapter(requireContext(), icons);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int iconResId = icons.get(position);
            Log.d("IconPickerDialog", "Selected icon: " + iconResId);
            if (listener != null) {
                Log.d("IconPickerDialog", "Listener not null");
                listener.onIconSelected(iconResId);
            }
            else {
                Log.d("IconPickerDialog", "Listener null");
            }
            dismiss();
        });

        builder.setView(dialogView); // GridView yerine root layout
        return builder.create();
    }
}
