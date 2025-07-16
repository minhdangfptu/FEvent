package com.fptu.fevent.ui.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.Task;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTasks(List<Task> newTasks) {
        taskList.clear();
        taskList.addAll(newTasks);
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDescription;
        private final TextView tvStatus;
        private final TextView tvDueDate;
        private final TextView tvAssignedTo;
        private final View statusIndicator;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvStatus = itemView.findViewById(R.id.tvTaskStatus);
            tvDueDate = itemView.findViewById(R.id.tvTaskDueDate);
            tvAssignedTo = itemView.findViewById(R.id.tvTaskAssignedTo);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
        }

        public void bind(Task task) {
            tvTitle.setText(task.title);
            tvDescription.setText(task.description);
            
            // Hiển thị trạng thái
            String status = task.status != null ? task.status : "Chưa bắt đầu";
            tvStatus.setText(status);
            
            // Màu sắc theo trạng thái
            int statusColor;
            switch (status.toLowerCase()) {
                case "hoàn thành":
                case "completed":
                    statusColor = R.color.green_500;
                    break;
                case "đang thực hiện":
                case "in_progress":
                    statusColor = R.color.blue_500;
                    break;
                case "quá hạn":
                case "overdue":
                    statusColor = R.color.red_500;
                    break;
                default:
                    statusColor = R.color.gray_500;
                    break;
            }
            
            statusIndicator.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), statusColor));
            
            // Hiển thị ngày hết hạn
            if (task.due_date != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvDueDate.setText("Hạn: " + sdf.format(task.due_date));
                tvDueDate.setVisibility(View.VISIBLE);
            } else {
                tvDueDate.setVisibility(View.GONE);
            }
            
            // Hiển thị người được giao
            if (task.assigned_to != null) {
                tvAssignedTo.setText("Giao cho cá nhân (ID: " + task.assigned_to + ")");
                tvAssignedTo.setVisibility(View.VISIBLE);
            } else if (task.team_id != null) {
                tvAssignedTo.setText("Giao cho ban (ID: " + task.team_id + ")");
                tvAssignedTo.setVisibility(View.VISIBLE);
            } else {
                tvAssignedTo.setText("Chưa giao");
                tvAssignedTo.setVisibility(View.VISIBLE);
            }
        }
    }
} 