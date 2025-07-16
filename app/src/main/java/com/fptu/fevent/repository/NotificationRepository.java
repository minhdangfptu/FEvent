package com.fptu.fevent.repository;

import android.app.Application;
import com.fptu.fevent.dao.NotificationDao;
import com.fptu.fevent.model.Notification;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationRepository {
    private final NotificationDao notificationDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public NotificationRepository(Application application) {
        notificationDao = com.fptu.fevent.database.AppDatabase.getInstance(application).notificationDao();
    }

    public List<Notification> getNotificationsByUserId(int userId) {
        return notificationDao.getNotificationsByUserId(userId);
    }

    public List<Notification> getUnreadNotificationsByUserId(int userId) {
        return notificationDao.getUnreadNotificationsByUserId(userId);
    }

    public int getUnreadNotificationCount(int userId) {
        return notificationDao.getUnreadNotificationCount(userId);
    }

    public void insert(Notification notification) {
        executor.execute(() -> notificationDao.insert(notification));
    }

    public void update(Notification notification) {
        executor.execute(() -> notificationDao.update(notification));
    }

    public void delete(Notification notification) {
        executor.execute(() -> notificationDao.delete(notification));
    }

    public void markAsRead(int notificationId) {
        executor.execute(() -> notificationDao.markAsRead(notificationId, new Date()));
    }

    public void markAllAsRead(int userId) {
        executor.execute(() -> notificationDao.markAllAsRead(userId, new Date()));
    }

    public List<Notification> getNotificationsByTaskAndType(int taskId, String type, int userId) {
        return notificationDao.getNotificationsByTaskAndType(taskId, type, userId);
    }

    public List<Notification> getNotificationsByScheduleAndType(int scheduleId, String type, int userId) {
        return notificationDao.getNotificationsByScheduleAndType(scheduleId, type, userId);
    }

    public void deleteOldNotifications(Date cutoffDate) {
        executor.execute(() -> notificationDao.deleteOldNotifications(cutoffDate));
    }

    // New methods for schedule notifications
    public List<Notification> getNotificationsByScheduleId(int scheduleId) {
        return notificationDao.getNotificationsByScheduleId(scheduleId);
    }

    public List<Notification> getNotificationsByTaskId(int taskId) {
        return notificationDao.getNotificationsByTaskId(taskId);
    }

    public List<Notification> getRecentTaskNotifications(int userId, String type, int taskId, Date timeLimit) {
        return notificationDao.getRecentTaskNotifications(userId, type, taskId, timeLimit);
    }

    public List<Notification> getRecentScheduleNotifications(int userId, String type, int scheduleId, Date timeLimit) {
        return notificationDao.getRecentScheduleNotifications(userId, type, scheduleId, timeLimit);
    }

    // Async methods for better performance
    public void getNotificationsByUserIdAsync(int userId, java.util.function.Consumer<List<Notification>> callback) {
        executor.execute(() -> {
            List<Notification> result = notificationDao.getNotificationsByUserId(userId);
            callback.accept(result);
        });
    }

    public void getUnreadNotificationCountAsync(int userId, java.util.function.Consumer<Integer> callback) {
        executor.execute(() -> {
            int count = notificationDao.getUnreadNotificationCount(userId);
            callback.accept(count);
        });
    }

    public void insertAsync(Notification notification, java.util.function.Consumer<Long> callback) {
        executor.execute(() -> {
            long id = notificationDao.insert(notification);
            if (callback != null) {
                callback.accept(id);
            }
        });
    }
} 