package com.example.eventtracker.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventtracker.R;
import com.example.eventtracker.data.model.HabitCheckEntity;
import com.example.eventtracker.data.model.HabitEntity;
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.ui.adapter.EventItem;
import com.example.eventtracker.viewmodel.HabitCheckViewModel;
import com.example.eventtracker.viewmodel.TaskViewModel;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventItem> eventList;
    private final TaskViewModel taskViewModel;
    private final HabitCheckViewModel habitCheckViewModel;
    private final LifecycleOwner lifecycleOwner;

    private String selectedDate;

    public EventAdapter(List<EventItem> eventList,
                        TaskViewModel taskViewModel,
                        HabitCheckViewModel habitCheckViewModel,
                        LifecycleOwner lifecycleOwner,
                        String selectedDate) {
        this.eventList = eventList;
        this.taskViewModel = taskViewModel;
        this.habitCheckViewModel = habitCheckViewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.selectedDate = selectedDate;
    }

    public interface OnItemClickListener {
        void onItemClick(EventItem item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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

        holder.checked.setOnCheckedChangeListener(null); // önce listener temizle

        if (item.getType() == EventItem.TYPE_TASK) {
            TaskEntity task = item.getTask();
            holder.icon.setImageResource(R.drawable.ic_task);
            holder.name.setText(task.getName());
            holder.time.setText(task.getTime());
            holder.type.setText("Task");
            holder.checked.setChecked(item.isChecked());
            holder.checked.setEnabled(true);

            holder.checked.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setChecked(isChecked);
                if (taskViewModel != null) {
                    task.setChecked(isChecked);
                    taskViewModel.update(task); // kalıcı güncelleme
                }
            });

        } else if (habitCheckViewModel != null) {
            HabitEntity habit = item.getHabit();
            holder.icon.setImageResource(R.drawable.ic_habit);
            holder.name.setText(habit.getName());
            holder.time.setText(habit.getTime());
            holder.type.setText("Habit");

            int habitId = habit.getId();
            String date = selectedDate;

            if (habit.isScheduledForDate(date)) {
                habitCheckViewModel.getHabitCheck(habitId, date).observe(lifecycleOwner, habitCheck -> {
                    holder.checked.setOnCheckedChangeListener(null);

                    boolean isChecked = habitCheck != null && habitCheck.isChecked();
                    holder.checked.setChecked(isChecked);
                    holder.checked.setEnabled(true);

                    holder.checked.setOnCheckedChangeListener((buttonView, newChecked) -> {
                        if (habitCheck != null) {
                            habitCheck.setChecked(newChecked);
                            habitCheckViewModel.update(habitCheck);
                        } else {
                            HabitCheckEntity newCheck = new HabitCheckEntity(habitId, date, newChecked);
                            habitCheckViewModel.insert(newCheck);
                        }
                    });
                });
            } else {
                holder.checked.setOnCheckedChangeListener(null);
                holder.checked.setChecked(false);
                holder.checked.setEnabled(false);
            }
        } else {
            Log.e("EventAdapter", "habitCheckViewModel is null!");
            holder.checked.setChecked(false);
            holder.checked.setEnabled(false);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    public void setEventList(List<EventItem> newList) {
        this.eventList = newList;
        notifyDataSetChanged();
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
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
            type = itemView.findViewById(R.id.eventType);
            checked = itemView.findViewById(R.id.eventChecker);
        }
    }
}
