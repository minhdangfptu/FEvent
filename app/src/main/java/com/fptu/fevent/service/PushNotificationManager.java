package com.fptu.fevent.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fptu.fevent.R;
import com.fptu.fevent.ui.notification.NotificationActivity;
import com.fptu.fevent.ui.task.TaskDetailActivity;
import com.fptu.fevent.ui.common.ScheduleDetailActivity;

public class PushNotificationManager {
    private static final String TAG = "PushNotificationManager";
    
    // Notification Channels
    private static final String CHANNEL_URGENT = "urgent_notifications";
    private static final String CHANNEL_HIGH = "high_notifications";
    private static final String CHANNEL_MEDIUM = "medium_notifications";
    private static final String CHANNEL_LOW = "low_notifications";
    
    private final Context context;
    private final NotificationManagerCompat notificationManager;

    public PushNotificationManager(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            
            // URGENT Channel - Công việc quá hạn, deadline 1h
            NotificationChannel urgentChannel = new NotificationChannel(
                CHANNEL_URGENT,
                "Thông báo Khẩn cấp",
                NotificationManager.IMPORTANCE_HIGH
            );
            urgentChannel.setDescription("Thông báo về công việc quá hạn và deadline gần");
            urgentChannel.enableLights(true);
            urgentChannel.setLightColor(Color.RED);
            urgentChannel.enableVibration(true);
            urgentChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            urgentChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
            manager.createNotificationChannel(urgentChannel);

            // HIGH Channel - Task mới, lịch họp 1h
            NotificationChannel highChannel = new NotificationChannel(
                CHANNEL_HIGH,
                "Thông báo Quan trọng",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            highChannel.setDescription("Thông báo về công việc mới và lịch họp sắp tới");
            highChannel.enableLights(true);
            highChannel.setLightColor(Color.YELLOW);
            highChannel.enableVibration(true);
            highChannel.setVibrationPattern(new long[]{200, 300});
            manager.createNotificationChannel(highChannel);

            // MEDIUM Channel - Thông báo team
            NotificationChannel mediumChannel = new NotificationChannel(
                CHANNEL_MEDIUM,
                "Thông báo Trung bình",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            mediumChannel.setDescription("Thông báo cho thành viên team");
            mediumChannel.enableLights(true);
            mediumChannel.setLightColor(Color.BLUE);
            manager.createNotificationChannel(mediumChannel);

            // LOW Channel - Thông báo chung
            NotificationChannel lowChannel = new NotificationChannel(
                CHANNEL_LOW,
                "Thông báo Chung",
                NotificationManager.IMPORTANCE_LOW
            );
            lowChannel.setDescription("Thông báo chung cho tất cả người dùng");
            lowChannel.enableLights(true);
            lowChannel.setLightColor(Color.GREEN);
            manager.createNotificationChannel(lowChannel);
        }
    }

    public void showPushNotification(com.fptu.fevent.model.Notification notification) {
        String channelId = getChannelIdForPriority(notification.priority);
        int icon = getIconForNotificationType(notification.type);
        
        // Tạo intent để mở app khi click notification
        Intent intent;
        if (notification.task_id != null) {
            // Mở TaskDetailActivity cho task notifications
            intent = new Intent(context, TaskDetailActivity.class);
            intent.putExtra("task_id", notification.task_id);
        } else if (notification.schedule_id != null) {
            // Mở ScheduleDetailActivity cho schedule notifications
            intent = new Intent(context, ScheduleDetailActivity.class);
            intent.putExtra("schedule_id", notification.schedule_id);
        } else {
            // Mở NotificationActivity cho general notifications
            intent = new Intent(context, NotificationActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            notification.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        // Tạo push notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
            .setSmallIcon(icon)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.message))
            .setPriority(getPriorityForNotification(notification.priority))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(getColorForNotificationType(notification.type))
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Thêm sound và vibration cho urgent notifications
        if ("URGENT".equals(notification.priority)) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                   .setVibrate(new long[]{100, 200, 300, 400, 500});
        }

        // Hiển thị notification
        try {
            notificationManager.notify(notification.id, builder.build());
            android.util.Log.d(TAG, "Push notification displayed: " + notification.title);
        } catch (SecurityException e) {
            android.util.Log.e(TAG, "Permission denied for showing notification", e);
        }
    }

    private String getChannelIdForPriority(String priority) {
        switch (priority) {
            case "URGENT": return CHANNEL_URGENT;
            case "HIGH": return CHANNEL_HIGH;
            case "MEDIUM": return CHANNEL_MEDIUM;
            case "LOW": return CHANNEL_LOW;
            default: return CHANNEL_MEDIUM;
        }
    }

    private int getPriorityForNotification(String priority) {
        switch (priority) {
            case "URGENT": return NotificationCompat.PRIORITY_HIGH;
            case "HIGH": return NotificationCompat.PRIORITY_DEFAULT;
            case "MEDIUM": return NotificationCompat.PRIORITY_DEFAULT;
            case "LOW": return NotificationCompat.PRIORITY_LOW;
            default: return NotificationCompat.PRIORITY_DEFAULT;
        }
    }

    private int getIconForNotificationType(String type) {
        switch (type) {
            case "TASK_ASSIGNED":
                return R.drawable.baseline_assignment_24;
            case "TASK_DEADLINE_1H":
            case "TASK_OVERDUE":
            case "TASK_DELAY":
                return R.drawable.baseline_assignment_late_24;
            case "SCHEDULE_CREATED":
            case "SCHEDULE_REMINDER_1H":
                return R.drawable.baseline_event_note_24;
            case "TASK_REMINDER":
                return R.drawable.baseline_notification_important_24;
            default:
                return R.drawable.baseline_notifications_24;
        }
    }

    private int getColorForNotificationType(String type) {
        switch (type) {
            case "TASK_ASSIGNED":
                return context.getResources().getColor(R.color.green_500, null);
            case "TASK_DEADLINE_1H":
            case "TASK_OVERDUE":
                return context.getResources().getColor(R.color.red_500, null);
            case "SCHEDULE_CREATED":
                return context.getResources().getColor(R.color.purple_500, null);
            case "SCHEDULE_REMINDER_1H":
                return context.getResources().getColor(R.color.orange_500, null);
            case "TASK_DELAY":
                return context.getResources().getColor(R.color.warning_yellow, null);
            default:
                return context.getResources().getColor(R.color.blue_500, null);
        }
    }

    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }

    public void cancelAllNotifications() {
        notificationManager.cancelAll();
    }
} 