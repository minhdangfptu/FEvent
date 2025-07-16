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
            
            // Sample notifications for different users and types
            createNotificationForUser(1, "Hệ thống", "Chào mừng bạn đến với FEvent!", "GENERAL", "LOW");
            createNotificationForUser(2, "Công việc quá hạn", "Công việc 'Thiết kế poster sự kiện' đã quá hạn 1 ngày", "TASK_OVERDUE", "HIGH");
            createNotificationForUser(3, "Nhắc nhở công việc", "Công việc 'Liên hệ khách mời' sẽ hết hạn trong 2 ngày", "TASK_REMINDER", "MEDIUM");
            createNotificationForUser(1, "Giao công việc mới", "Bạn được giao công việc 'Chuẩn bị hội trường'. Hạn hoàn thành: 25/01/2024", "TASK_ASSIGNED", "HIGH");
            createNotificationForUser(2, "Công việc sắp hết hạn", "Công việc 'Viết báo cáo' sẽ hết hạn trong vòng 1 giờ tới", "TASK_DEADLINE_1H", "URGENT");
            createNotificationForUser(1, "Lịch họp mới", "Quản lý đã tạo lịch họp 'Họp định kỳ tuần' vào 26/01/2024 15:00 tại Phòng họp A", "SCHEDULE_CREATED", "MEDIUM");
            createNotificationForUser(3, "Cuộc họp sắp bắt đầu", "Cuộc họp 'Review dự án' sẽ bắt đầu trong vòng 1 giờ tới tại Phòng họp B", "SCHEDULE_REMINDER_1H", "HIGH");
            
            Log.d(TAG, "Sample notifications created");
        });
    }

    private void createNotificationForUser(int userId, String title, String message, String type, String priority) {
        Notification notification = new Notification(userId, title, message, type, priority);
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

    public void createTaskAssignmentNotification(int userId, int taskId, String taskTitle, String dueDate) {
        String title = "Bạn được giao công việc mới";
        String message = String.format("Bạn đã được giao công việc '%s'. Hạn hoàn thành: %s. Hãy bắt đầu thực hiện ngay!", taskTitle, dueDate);
        
        Notification notification = new Notification(userId, taskId, title, message, "TASK_ASSIGNED", "HIGH");
        notificationRepository.insert(notification);
    }

    public void createTaskDeadline1HNotification(int userId, int taskId, String taskTitle) {
        String title = "Công việc sắp hết hạn trong 1 giờ";
        String message = String.format("Công việc '%s' sẽ hết hạn trong vòng 1 giờ tới. Hãy hoàn thành ngay!", taskTitle);
        
        Notification notification = new Notification(userId, taskId, title, message, "TASK_DEADLINE_1H", "URGENT");
        notificationRepository.insert(notification);
    }

    public void createScheduleCreatedNotification(int userId, int scheduleId, String scheduleTitle, String startTime, String location, String creatorName) {
        String title = "Lịch họp mới được tạo";
        String message = String.format("%s đã tạo lịch họp '%s' vào %s tại %s. Hãy sắp xếp thời gian tham gia!", 
            creatorName, scheduleTitle, startTime, location);
        
        Notification notification = new Notification(userId, scheduleId, title, message, "SCHEDULE_CREATED", "MEDIUM", true);
        notificationRepository.insert(notification);
    }

    public void createScheduleReminder1HNotification(int userId, int scheduleId, String scheduleTitle, String location) {
        String title = "Cuộc họp sắp bắt đầu trong 1 giờ";
        String message = String.format("Cuộc họp '%s' sẽ bắt đầu trong vòng 1 giờ tới tại %s. Hãy chuẩn bị sẵn sàng!", 
            scheduleTitle, location);
        
        Notification notification = new Notification(userId, scheduleId, title, message, "SCHEDULE_REMINDER_1H", "HIGH", true);
        notificationRepository.insert(notification);
    }

    public void createGeneralNotification(int userId, String title, String message) {
        Notification notification = new Notification(userId, title, message, "GENERAL", "LOW");
        notificationRepository.insert(notification);
    }

    // Test methods for schedule notifications
    public void testScheduleNotifications() {
        executor.execute(() -> {
            Log.d(TAG, "Testing schedule notifications with mock data");
            
            // Test SCHEDULE_CREATED notifications
            createScheduleCreatedNotification(1, 1, "Họp ban Core - Chuẩn bị sự kiện Halloween", "31/12/2024 14:00", "Phòng họp A101", "Quản Trị Viên");
            createScheduleCreatedNotification(2, 2, "Họp ban Truyền thông - Review content", "Hôm nay 14:00", "Phòng họp B202", "Lê Thị Linh");
            createScheduleCreatedNotification(3, 3, "Họp tổng toàn thể - Kick-off Q1 2025", "Ngày mai 09:00", "Hội trường chính", "Nguyễn Sơn");
            
            // Test SCHEDULE_REMINDER_1H notifications
            createScheduleReminder1HNotification(1, 2, "Họp ban Truyền thông - Review content", "Phòng họp B202");
            createScheduleReminder1HNotification(2, 4, "Họp ban Media - Urgent design review", "Phòng Design Studio");
            createScheduleReminder1HNotification(3, 2, "Họp ban Truyền thông - Review content", "Phòng họp B202");
            
            Log.d(TAG, "Schedule notifications test completed");
        });
    }

    public void testScheduleCreatedNotification() {
        executor.execute(() -> {
            Log.d(TAG, "Testing SCHEDULE_CREATED notification");
            
            // Test notification for schedule created by admin
            createScheduleCreatedNotification(1, 1, "Họp khẩn cấp - Xử lý sự cố", "15:30 hôm nay", "Phòng họp A101", "Quản Trị Viên");
            createScheduleCreatedNotification(2, 1, "Họp khẩn cấp - Xử lý sự cố", "15:30 hôm nay", "Phòng họp A101", "Quản Trị Viên");
            createScheduleCreatedNotification(3, 1, "Họp khẩn cấp - Xử lý sự cố", "15:30 hôm nay", "Phòng họp A101", "Quản Trị Viên");
            
            Log.d(TAG, "SCHEDULE_CREATED notification test completed");
        });
    }

    public void testScheduleReminder1HNotification() {
        executor.execute(() -> {
            Log.d(TAG, "Testing SCHEDULE_REMINDER_1H notification");
            
            // Test reminder notification for meeting starting in 1 hour
            createScheduleReminder1HNotification(1, 2, "Họp review tiến độ dự án", "Phòng họp B202");
            createScheduleReminder1HNotification(2, 2, "Họp review tiến độ dự án", "Phòng họp B202");
            createScheduleReminder1HNotification(3, 2, "Họp review tiến độ dự án", "Phòng họp B202");
            
            Log.d(TAG, "SCHEDULE_REMINDER_1H notification test completed");
        });
    }

    public void triggerScheduleReminderCheck() {
        executor.execute(() -> {
            Log.d(TAG, "Triggering schedule reminder check");
            notificationService.checkScheduleReminders();
            Log.d(TAG, "Schedule reminder check completed");
        });
    }

    public void triggerTaskDeadlineCheck() {
        executor.execute(() -> {
            Log.d(TAG, "Triggering task deadline check");
            notificationService.checkTaskDelaysAndNotify();
            Log.d(TAG, "Task deadline check completed");
        });
    }

    public void testScheduleNotificationFixes() {
        executor.execute(() -> {
            Log.d(TAG, "Testing schedule notification fixes");
            
            // Test 1: Schedule with team_id (should notify team members + all users)
            createScheduleCreatedNotification(1, 1, "Họp ban Core - Team meeting", "14:00 hôm nay", "Phòng A101", "Admin");
            createScheduleCreatedNotification(2, 1, "Họp ban Core - Team meeting", "14:00 hôm nay", "Phòng A101", "Admin");
            createScheduleCreatedNotification(3, 1, "Họp ban Core - Team meeting", "14:00 hôm nay", "Phòng A101", "Admin");
            
            // Test 2: Schedule without team_id (should notify all users with appropriate priority)
            createScheduleCreatedNotification(1, 2, "Họp tổng toàn thể - General meeting", "09:00 ngày mai", "Hội trường", "Admin");
            createScheduleCreatedNotification(2, 2, "Họp tổng toàn thể - General meeting", "09:00 ngày mai", "Hội trường", "Admin");
            createScheduleCreatedNotification(3, 2, "Họp tổng toàn thể - General meeting", "09:00 ngày mai", "Hội trường", "Admin");
            
            // Test 3: Schedule reminders for team meetings
            createScheduleReminder1HNotification(1, 1, "Họp ban Core - Team meeting", "Phòng A101");
            createScheduleReminder1HNotification(2, 1, "Họp ban Core - Team meeting", "Phòng A101");
            createScheduleReminder1HNotification(3, 1, "Họp ban Core - Team meeting", "Phòng A101");
            
            // Test 4: Schedule reminders for general meetings
            createScheduleReminder1HNotification(1, 2, "Họp tổng toàn thể - General meeting", "Hội trường");
            createScheduleReminder1HNotification(2, 2, "Họp tổng toàn thể - General meeting", "Hội trường");
            createScheduleReminder1HNotification(3, 2, "Họp tổng toàn thể - General meeting", "Hội trường");
            
            Log.d(TAG, "Schedule notification fixes test completed");
        });
    }

    public void testScheduleDetailNavigation() {
        executor.execute(() -> {
            Log.d(TAG, "Testing schedule detail navigation");
            
            // Create notifications that will test the navigation fix
            createScheduleCreatedNotification(1, 1, "Test Navigation - Schedule 1", "14:00", "Test Room", "Admin");
            createScheduleReminder1HNotification(1, 2, "Test Navigation - Schedule 2", "Test Room 2");
            
            Log.d(TAG, "Schedule detail navigation test completed - click notifications to test");
        });
    }

    public void testNewScheduleDetailInterface() {
        executor.execute(() -> {
            Log.d(TAG, "Testing new schedule detail interface");
            
            // Create various types of schedule notifications to test the new interface
            createScheduleCreatedNotification(1, 1, "Họp ban Core - Chuẩn bị sự kiện Halloween", "25/01/2024 14:00", "Phòng họp A101", "Quản Trị Viên");
            createScheduleCreatedNotification(1, 2, "Họp tổng toàn thể - Kick-off Q1 2025", "26/01/2024 09:00", "Hội trường chính", "Nguyễn Sơn");
            createScheduleReminder1HNotification(1, 3, "Họp ban Truyền thông - Review content", "Phòng họp B202");
            createScheduleReminder1HNotification(1, 4, "Họp khẩn cấp - Xử lý sự cố", "Phòng họp Emergency");
            
            Log.d(TAG, "Created test notifications for new schedule detail interface - click to see the new design!");
        });
    }
} 