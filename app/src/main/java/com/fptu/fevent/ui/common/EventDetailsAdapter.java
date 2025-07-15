package com.fptu.fevent.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.fevent.ui.common.ScheduleFragment.CalendarItem;
import com.fptu.fevent.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventDetailsAdapter extends RecyclerView.Adapter<EventDetailsAdapter.ViewHolder> {

    private List<CalendarItem> items;

    private SimpleDateFormat timeFormat;

    public EventDetailsAdapter(List<CalendarItem> items) {
        this.items = items;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalendarItem item = items.get(position);
        
        holder.tvTitle.setText(item.title);
        holder.tvSubtitle.setText(item.subtitle);
        
        // Set description
        if (item.description != null && !item.description.trim().isEmpty()) {
            holder.tvDescription.setText(item.description);
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }
        
        // Set time
        String timeText = "";
        if (item.startTime != null) {
            timeText = timeFormat.format(item.startTime);
            if (item.endTime != null) {
                timeText += " - " + timeFormat.format(item.endTime);
            }
        }
        
        if (!timeText.isEmpty()) {
            holder.tvTime.setText(timeText);
            holder.tvTime.setVisibility(View.VISIBLE);
        } else {
            holder.tvTime.setVisibility(View.GONE);
        }
        
        // Set color indicator
        int color = ContextCompat.getColor(holder.itemView.getContext(), item.colorRes);
        holder.colorIndicator.setBackgroundColor(color);
        
        // Set type icon based on item type
        int iconRes;
        switch (item.type) {
            case EventTimelineActivity.CalendarItem.TYPE_TASK:
                iconRes = R.drawable.baseline_assignment_24;
                break;
            case EventTimelineActivity.CalendarItem.TYPE_SCHEDULE:
                iconRes = R.drawable.baseline_access_time_24;
                break;
            case EventTimelineActivity.CalendarItem.TYPE_EVENT:
                iconRes = R.drawable.baseline_event_24;
                break;
            default:
                iconRes = R.drawable.baseline_circle_24;
                break;
        }
        
        holder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
        holder.tvTitle.setCompoundDrawablePadding(16);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle, tvDescription, tvTime;
        View colorIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
        }
    }
} 