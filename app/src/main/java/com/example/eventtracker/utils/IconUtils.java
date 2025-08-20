package com.example.eventtracker.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.example.eventtracker.R;

import java.util.ArrayList;
import java.util.List;

public class IconUtils {

    public static List<Integer> getAllIcons(Context context) {
        List<Integer> icons = new ArrayList<>();
        TypedArray ta = context.getResources().obtainTypedArray(R.array.task_icons);

        for (int i = 0; i < ta.length(); i++) {
            icons.add(ta.getResourceId(i, 0));
        }

        ta.recycle();
        return icons;
    }
}
