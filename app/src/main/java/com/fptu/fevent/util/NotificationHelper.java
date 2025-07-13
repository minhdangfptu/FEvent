package com.fptu.fevent.util;

import android.app.Application;
import android.util.Log;

import com.fptu.fevent.model.Notification;
import com.fptu.fevent.repository.NotificationRepository;
import com.fptu.fevent.service.NotificationService;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";
    
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public NotificationHelper(Application application) {
        this.notificationRepository = new NotificationRepository(application);
        this.notificationService = new NotificationService(application);
    }

    public void createSampleNotifications() {
        executor.execute(() -> {
            Log.d(TAG, "Creating sample notifications");
            
            // Sample notifications for different users
            createNotificationForUser(1, "Hệ thống", "Chào mừng bạn đến với FEvent!", "GENERAL", "LOW");
            createNotificationForUser(2, "Công việc quá hạn", "Công việc 'Thiết kế poster sự kiện' đã quá hạn 1 ngày", "TASK_OVERDUE", "HIGH");
            createNotificationForUser(3, "Nhắc nhở công việc", "Công việc 'Liên hệ khách mời' sẽ hết hạn trong 2 ngày", "TASK_REMINDER", "MEDIUM");
            
            Log.d(TAG, "Sample notifications created");
        });
    }

    private void createNotificationForUser(int userId, String title, String message, String type, String priority) {
        Notification notification = new Notification(userId, null, title, message, type, priority);
        notificationRepository.insert(notification);
    }

    public void triggerTaskDelayCheck() {
        notificationService.checkTaskDelaysAndNotify();
    }

    public void cleanupOldNotifications() {
        notificationService.cleanupOldNotifications();
    }

    public void createTaskDelayNotification(int userId, int taskId, String taskTitle, int daysOverdue) {
        String title = "Công việc quá hạn";
        String message = String.format("Công việc '%s' đã quá hạn %d ngày. Vui lòng hoàn thành ngay.", taskTitle, daysOverdue);
        
        Notification notification = new Notification(userId, taskId, title, message, "TASK_OVERDUE", "HIGH");
        notificationRepository.insert(notification);
    }

    public void createTaskReminderNotification(int userId, int taskId, String taskTitle, int daysUntilDue) {
        String title = "Nhắc nhở công việc";
        String timeText = daysUntilDue == 0 ? "hôm nay" : daysUntilDue + " ngày nữa";
        String message = String.format("Công việc '%s' sẽ hết hạn %s. Vui lòng hoàn thành đúng thời hạn.", taskTitle, timeText);
        
        String priority = daysUntilDue == 0 ? "HIGH" : "MEDIUM";
        Notification notification = new Notification(userId, taskId, title, message, "TASK_REMINDER", priority);
        notificationRepository.insert(notification);
    }

    public void createGeneralNotification(int userId, String title, String message) {
        Notification notification = new Notification(userId, null, title, message, "GENERAL", "LOW");
        notificationRepository.insert(notification);
    }
} 