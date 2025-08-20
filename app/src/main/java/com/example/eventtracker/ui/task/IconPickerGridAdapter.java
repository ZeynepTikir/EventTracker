package com.example.eventtracker.ui.task;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class IconPickerGridAdapter extends BaseAdapter {

    private final Context context;
    private final List<Integer> icons;

    public IconPickerGridAdapter(Context context, List<Integer> icons) {
        this.context = context;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return icons.size();
    }

    @Override
    public Object getItem(int position) {
        return icons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (convertView instanceof ImageView)
                ? (ImageView) convertView
                : new ImageView(context);

        imageView.setImageResource(icons.get(position));
        imageView.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
        imageView.setPadding(16, 16, 16, 16);

        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        return imageView;
    }
}
