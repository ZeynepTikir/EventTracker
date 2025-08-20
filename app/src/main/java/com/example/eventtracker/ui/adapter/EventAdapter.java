package com.example.eventtracker.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventtracker.R;
import com.example.eventtracker.data.model.TaskEntity;
import com.example.eventtracker.viewmodel.TaskViewModel;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<TaskEntity> taskList;
    private final TaskViewModel taskViewModel;

    public EventAdapter(List<TaskEntity> taskList, TaskViewModel taskViewModel) {
        this.taskList = taskList;
        this.taskViewModel = taskViewModel;
    }

    public interface OnItemClickListener {
        void onItemClick(TaskEntity task);
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
        TaskEntity task = taskList.get(position);

        // Eski listener'ı temizle
        holder.checked.setOnCheckedChangeListener(null);

        // Icon'u String'den resource ID'ye çevirip set et
        String iconName = task.getIcon(); // örn: "ic_task"
        int iconResId = holder.icon.getContext()
                .getResources()
                .getIdentifier(iconName, "drawable", holder.icon.getContext().getPackageName());
        if (iconResId != 0) {
            holder.icon.setImageResource(iconResId);
        } else {
            holder.icon.setImageResource(R.drawable.ic_task); // default icon
        }

        holder.name.setText(task.getName());
        holder.time.setText(task.getTime());
        holder.checked.setChecked(task.isChecked());
        holder.checked.setEnabled(true);

        // Checked değişimini DB'ye yansıt
        holder.checked.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setChecked(isChecked);
            taskViewModel.update(task); // Room DB güncellemesi
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(task);
        });
    }


    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    public void setTaskList(List<TaskEntity> newList) {
        this.taskList = newList;
        notifyDataSetChanged();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name, time;
        CheckBox checked;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.eventIcon);
            name = itemView.findViewById(R.id.eventName);
            time = itemView.findViewById(R.id.eventTime);
            checked = itemView.findViewById(R.id.eventChecker);
        }
    }
}
