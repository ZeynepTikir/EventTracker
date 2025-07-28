package com.example.eventtracker.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventtracker.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventItem> eventList;

    public EventAdapter(List<EventItem> eventList) {
        this.eventList = eventList;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(EventItem item, boolean isChecked);
    }

    private OnCheckedChangeListener checkedChangeListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.checkedChangeListener = listener;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventItem item = eventList.get(position);

        if (item.getType() == EventItem.TYPE_TASK) {
            holder.icon.setImageResource(R.drawable.ic_task);
            holder.name.setText(item.getTask().getName());
            holder.time.setText(item.getTask().getTime());
            holder.type.setText("Task");
        } else {
            holder.icon.setImageResource(R.drawable.ic_habit);
            holder.name.setText(item.getHabit().getName());
            holder.time.setText(item.getHabit().getTime());
            holder.type.setText("Habit");
        }

        holder.checked.setOnCheckedChangeListener(null);
        holder.checked.setChecked(item.isChecked());

        holder.checked.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("Adapter Checkbox", String.valueOf(isChecked));
            item.setChecked(isChecked);

            if (checkedChangeListener != null) {
                checkedChangeListener.onCheckedChanged(item, isChecked);
            }
        });


        //Log.d("BIND_VIEW", "Position " + position + ", Time: " + (item.getType() == EventItem.TYPE_TASK ? item.getTask().getTime() : item.getHabit().getTime()));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name, time, type;
        CheckBox checked;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.eventIcon);
            name = itemView.findViewById(R.id.eventName);
            time = itemView.findViewById(R.id.eventTime);
            type = itemView.findViewById(R.id.eventType); // EKLENDİ: Tip bilgisi
            checked = itemView.findViewById(R.id.eventChecker);
        }
    }
}
