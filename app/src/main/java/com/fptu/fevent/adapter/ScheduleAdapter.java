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

    private final List<Schedule> scheduleList;
    private final OnItemClickListener listener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public ScheduleAdapter(List<Schedule> scheduleList, OnItemClickListener listener) {
        this.scheduleList = scheduleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        holder.tvTitle.setText(schedule.title);
        holder.tvTime.setText(
                sdf.format(schedule.start_time) + " - " + sdf.format(schedule.end_time)
        );
        holder.tvLocation.setText(schedule.location);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(schedule));
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvLocation;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_schedule_title);
            tvTime = itemView.findViewById(R.id.tv_schedule_time);
            tvLocation = itemView.findViewById(R.id.tv_schedule_location);
        }
    }
}
