package com.fptu.fevent.ui.common;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.model.Schedule;
import com.fptu.fevent.model.Task;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    
    private List<CalendarDay> calendarDays;
    private List<Task> tasks;
    private List<Schedule> schedules;
    private List<EventInfo> events;
    private OnDateClickListener onDateClickListener;
    private Date selectedDate;
    
    public interface OnDateClickListener {
        void onDateClick(Date date);
    }
    
    public CalendarAdapter(List<CalendarDay> calendarDays, OnDateClickListener listener) {
        this.calendarDays = calendarDays;
        this.onDateClickListener = listener;
    }
    
    public void updateData(List<Task> tasks, List<Schedule> schedules, List<EventInfo> events) {
        this.tasks = tasks;
        this.schedules = schedules;
        this.events = events;
        notifyDataSetChanged();
    }
    
    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDay day = calendarDays.get(position);
        
        // Set day number
        if (day.dayNumber > 0) {
            holder.tvDayNumber.setText(String.valueOf(day.dayNumber));
            holder.tvDayNumber.setVisibility(View.VISIBLE);
            
            // Check if this day has events/tasks
            boolean hasTask = hasTaskOnDate(day.date);
            boolean hasSchedule = hasScheduleOnDate(day.date);
            boolean hasEvent = hasEventOnDate(day.date);
            
            // Show indicators
            holder.indicatorTask.setVisibility(hasTask ? View.VISIBLE : View.GONE);
            holder.indicatorSchedule.setVisibility(hasSchedule ? View.VISIBLE : View.GONE);
            holder.indicatorEvent.setVisibility(hasEvent ? View.VISIBLE : View.GONE);
            
            // Set background for selected date
            if (selectedDate != null && isSameDay(day.date, selectedDate)) {
                holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.blue_100));
                holder.cardView.setStrokeColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.blue_500));
                holder.cardView.setStrokeWidth(4);
            } else {
                holder.cardView.setCardBackgroundColor(Color.WHITE);
                holder.cardView.setStrokeColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.gray_300));
                holder.cardView.setStrokeWidth(2);
            }
            
            // Set text color based on day type
            if (day.isCurrentMonth) {
                if (day.isToday) {
                    holder.tvDayNumber.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.blue_600));
                } else if (day.isSunday) {
                    holder.tvDayNumber.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.red_500));
                } else {
                    holder.tvDayNumber.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.gray_800));
                }
            } else {
                holder.tvDayNumber.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.gray_400));
            }
            
            // Set click listener
            holder.itemView.setOnClickListener(v -> {
                if (onDateClickListener != null) {
                    onDateClickListener.onDateClick(day.date);
                }
            });
            
        } else {
            // Empty cell
            holder.tvDayNumber.setVisibility(View.INVISIBLE);
            holder.indicatorTask.setVisibility(View.GONE);
            holder.indicatorSchedule.setVisibility(View.GONE);
            holder.indicatorEvent.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(Color.TRANSPARENT);
            holder.cardView.setStrokeWidth(0);
            holder.itemView.setOnClickListener(null);
        }
    }
    
    @Override
    public int getItemCount() {
        return calendarDays.size();
    }
    
    private boolean hasTaskOnDate(Date date) {
        if (tasks == null || date == null) return false;
        
        for (Task task : tasks) {
            if (task.due_date != null && isSameDay(task.due_date, date)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasScheduleOnDate(Date date) {
        if (schedules == null || date == null) return false;
        
        for (Schedule schedule : schedules) {
            if (schedule.start_time != null && isSameDay(schedule.start_time, date)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasEventOnDate(Date date) {
        if (events == null || date == null) return false;
        
        for (EventInfo event : events) {
            if (event.start_time != null && isSameDay(event.start_time, date)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    
    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvDayNumber;
        View indicatorTask;
        View indicatorSchedule;
        View indicatorEvent;
        
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
            indicatorTask = itemView.findViewById(R.id.indicatorTask);
            indicatorSchedule = itemView.findViewById(R.id.indicatorSchedule);
            indicatorEvent = itemView.findViewById(R.id.indicatorEvent);
        }
    }
    
    public static class CalendarDay {
        public int dayNumber;
        public Date date;
        public boolean isCurrentMonth;
        public boolean isToday;
        public boolean isSunday;
        
        public CalendarDay(int dayNumber, Date date, boolean isCurrentMonth, boolean isToday, boolean isSunday) {
            this.dayNumber = dayNumber;
            this.date = date;
            this.isCurrentMonth = isCurrentMonth;
            this.isToday = isToday;
            this.isSunday = isSunday;
        }
    }
} 