package com.example.eventtracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventtracker.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddSelectionBottomSheet extends BottomSheetDialogFragment {

    public interface OnSelectionListener {
        void onAddTaskSelected();
        void onAddHabitSelected();
    }

    private OnSelectionListener listener;

    public void setOnSelectionListener(OnSelectionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_selection, container, false);

        Button btnAddTask = view.findViewById(R.id.btn_add_task);
        Button btnAddHabit = view.findViewById(R.id.btn_add_habit);

        btnAddTask.setOnClickListener(v -> {
            if (listener != null) listener.onAddTaskSelected();
            dismiss();
        });

        btnAddHabit.setOnClickListener(v -> {
            if (listener != null) listener.onAddHabitSelected();
            dismiss();
        });

        return view;
    }
}
