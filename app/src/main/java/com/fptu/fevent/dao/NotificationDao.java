package com.fptu.fevent.dao;

import androidx.room.*;
import com.fptu.fevent.model.Notification;
import java.util.List;

@Dao
public interface NotificationDao {
    @Insert
    long insert(Notification notification);

    @Update
    void update(Notification notification);

    @Delete
    void delete(Notification notification);

    @Query("SELECT * FROM Notification WHERE user_id = :userId ORDER BY created_at DESC")
    List<Notification> getNotificationsByUserId(int userId);

    @Query("SELECT * FROM Notification WHERE user_id = :userId AND is_read = 0 ORDER BY created_at DESC")
    List<Notification> getUnreadNotificationsByUserId(int userId);

    @Query("SELECT COUNT(*) FROM Notification WHERE user_id = :userId AND is_read = 0")
    int getUnreadNotificationCount(int userId);

    @Query("UPDATE Notification SET is_read = 1, read_at = :readAt WHERE id = :notificationId")
    void markAsRead(int notificationId, java.util.Date readAt);

    @Query("UPDATE Notification SET is_read = 1, read_at = :readAt WHERE user_id = :userId")
    void markAllAsRead(int userId, java.util.Date readAt);

    @Query("SELECT * FROM Notification WHERE task_id = :taskId AND type = :type AND user_id = :userId")
    List<Notification> getNotificationsByTaskAndType(int taskId, String type, int userId);

    @Query("SELECT * FROM Notification WHERE schedule_id = :scheduleId AND type = :type AND user_id = :userId")
    List<Notification> getNotificationsByScheduleAndType(int scheduleId, String type, int userId);

    @Query("DELETE FROM Notification WHERE created_at < :cutoffDate")
    void deleteOldNotifications(java.util.Date cutoffDate);

    @Query("SELECT * FROM Notification WHERE type = :type AND user_id = :userId ORDER BY created_at DESC")
    List<Notification> getNotificationsByType(String type, int userId);

    @Query("SELECT * FROM Notification WHERE priority = :priority AND user_id = :userId ORDER BY created_at DESC")
    List<Notification> getNotificationsByPriority(String priority, int userId);

    @Insert
    void insertAll(Notification... notifications);

    @Query("SELECT COUNT(*) FROM Notification")
    int getCount();

    // New queries for schedule notifications
    @Query("SELECT * FROM Notification WHERE schedule_id = :scheduleId")
    List<Notification> getNotificationsByScheduleId(int scheduleId);

    @Query("SELECT * FROM Notification WHERE task_id = :taskId")
    List<Notification> getNotificationsByTaskId(int taskId);

    // Query to check for existing similar notifications to prevent spam
    @Query("SELECT * FROM Notification WHERE user_id = :userId AND type = :type AND task_id = :taskId AND created_at > :timeLimit")
    List<Notification> getRecentTaskNotifications(int userId, String type, int taskId, java.util.Date timeLimit);

    @Query("SELECT * FROM Notification WHERE user_id = :userId AND type = :type AND schedule_id = :scheduleId AND created_at > :timeLimit")
    List<Notification> getRecentScheduleNotifications(int userId, String type, int scheduleId, java.util.Date timeLimit);
} 