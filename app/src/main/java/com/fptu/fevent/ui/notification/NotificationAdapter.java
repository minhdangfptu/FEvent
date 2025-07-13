package com.fptu.fevent.ui.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<Notification> notificationList;
    private final OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationAdapter(List<Notification> notificationList, OnNotificationClickListener listener) {
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.bind(notification);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void updateNotifications(List<Notification> newNotifications) {
        notificationList.clear();
        notificationList.addAll(newNotifications);
        notifyDataSetChanged();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final View priorityIndicator;
        private final ImageView ivNotificationIcon;
        private final TextView tvNotificationTitle;
        private final TextView tvNotificationMessage;
        private final TextView tvNotificationTime;
        private final TextView tvNotificationType;
        private final View unreadIndicator;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            priorityIndicator = itemView.findViewById(R.id.priorityIndicator);
            ivNotificationIcon = itemView.findViewById(R.id.ivNotificationIcon);
            tvNotificationTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvNotificationMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvNotificationTime = itemView.findViewById(R.id.tvNotificationTime);
            tvNotificationType = itemView.findViewById(R.id.tvNotificationType);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
        }

        public void bind(Notification notification) {
            tvNotificationTitle.setText(notification.title);
            tvNotificationMessage.setText(notification.message);
            tvNotificationTime.setText(getTimeAgo(notification.created_at));
            
            // Set notification type and icon
            setNotificationTypeAndIcon(notification);
            
            // Set priority indicator color
            setPriorityIndicator(notification.priority);
            
            // Show/hide unread indicator
            unreadIndicator.setVisibility(notification.is_read ? View.GONE : View.VISIBLE);
            
            // Set background for unread notifications
            if (!notification.is_read) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.blue_50));
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
            }
        }

        private void setNotificationTypeAndIcon(Notification notification) {
            int iconRes;
            int iconColor;
            String typeText;
            
            switch (notification.type) {
                case "TASK_DELAY":
                    iconRes = R.drawable.baseline_assignment_late_24;
                    iconColor = R.color.orange_500;
                    typeText = "Chậm tiến độ";
                    break;
                case "TASK_OVERDUE":
                    iconRes = R.drawable.baseline_assignment_late_24;
                    iconColor = R.color.red_500;
                    typeText = "Quá hạn";
                    break;
                case "TASK_REMINDER":
                    iconRes = R.drawable.baseline_assignment_24;
                    iconColor = R.color.blue_500;
                    typeText = "Nhắc nhở";
                    break;
                case "GENERAL":
                    iconRes = R.drawable.baseline_circle_notifications_24;
                    iconColor = R.color.gray_600;
                    typeText = "Thông báo";
                    break;
                default:
                    iconRes = R.drawable.baseline_circle_notifications_24;
                    iconColor = R.color.blue_500;
                    typeText = "Thông báo";
                    break;
            }
            
            ivNotificationIcon.setImageResource(iconRes);
            ivNotificationIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), iconColor));
            tvNotificationType.setText(typeText);
        }

        private void setPriorityIndicator(String priority) {
            int color;
            
            switch (priority) {
                case "URGENT":
                    color = R.color.red_500;
                    break;
                case "HIGH":
                    color = R.color.orange_500;
                    break;
                case "MEDIUM":
                    color = R.color.blue_500;
                    break;
                case "LOW":
                    color = R.color.gray_400;
                    break;
                default:
                    color = R.color.blue_500;
                    break;
            }
            
            priorityIndicator.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), color));
        }

        private String getTimeAgo(Date date) {
            if (date == null) return "";
            
            long diffInMs = System.currentTimeMillis() - date.getTime();
            
            if (diffInMs < TimeUnit.MINUTES.toMillis(1)) {
                return "Vừa xong";
            } else if (diffInMs < TimeUnit.HOURS.toMillis(1)) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMs);
                return minutes + " phút trước";
            } else if (diffInMs < TimeUnit.DAYS.toMillis(1)) {
                long hours = TimeUnit.MILLISECONDS.toHours(diffInMs);
                return hours + " giờ trước";
            } else if (diffInMs < TimeUnit.DAYS.toMillis(7)) {
                long days = TimeUnit.MILLISECONDS.toDays(diffInMs);
                return days + " ngày trước";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return sdf.format(date);
            }
        }
    }
} 