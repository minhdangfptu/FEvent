package com.fptu.fevent.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.fevent.R;
import com.fptu.fevent.model.Schedule;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Schedule schedule);
    }

        public interface OnScheduleClickListener {
        void onEditClick(Schedule schedule);
        void onDeleteClick(Schedule schedule);
    }

    private List<Schedule> scheduleList;
    private final OnItemClickListener itemClickListener;
    private final OnScheduleClickListener actionListener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public ScheduleAdapter(List<Schedule> scheduleList, OnItemClickListener listener) {
        this.scheduleList = scheduleList;
        this.itemClickListener = listener;
        this.actionListener = null;
    }

    public ScheduleAdapter(List<Schedule> scheduleList,
                           OnScheduleClickListener actionListener,
                           OnItemClickListener itemListener) {
        this.scheduleList = scheduleList;
        this.actionListener = actionListener;
        this.itemClickListener = itemListener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        holder.bind(schedule);
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public void updateSchedules(List<Schedule> newSchedules) {
        this.scheduleList = newSchedules;
        notifyDataSetChanged();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvLocation, tvDescription;
        View btnEdit, btnDelete;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_schedule_title);
            tvTime = itemView.findViewById(R.id.tv_schedule_time);
            tvLocation = itemView.findViewById(R.id.tv_schedule_location);
            tvDescription = itemView.findViewById(R.id.tv_schedule_description);
            btnEdit = itemView.findViewById(R.id.btn_edit_item);
            btnDelete = itemView.findViewById(R.id.btn_delete_item);
        }

        public void bind(Schedule schedule) {
            tvTitle.setText(schedule.title);
            tvLocation.setText(schedule.location);
            if (tvDescription != null) {
                tvDescription.setText(schedule.description);
            }

            String timeText = sdf.format(schedule.start_time) + " - " + sdf.format(schedule.end_time);
            tvTime.setText(timeText);

            // Set up item click listener (for ScheduleListActivity)
            if (itemClickListener != null) {
                itemView.setOnClickListener(v -> itemClickListener.onItemClick(schedule));
            }

            // Set up action buttons (for ScheduleActivity)
            if (actionListener != null && btnEdit != null && btnDelete != null) {
                btnEdit.setOnClickListener(v -> actionListener.onEditClick(schedule));
                btnDelete.setOnClickListener(v -> actionListener.onDeleteClick(schedule));
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
            } else if (btnEdit != null && btnDelete != null) {
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }
        }
    }
}