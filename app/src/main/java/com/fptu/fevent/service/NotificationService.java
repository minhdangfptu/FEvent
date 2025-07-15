package com.fptu.fevent.service;

import android.app.Application;
import android.util.Log;

import com.fptu.fevent.model.Notification;
import com.fptu.fevent.model.Task;
import com.fptu.fevent.model.User;
import com.fptu.fevent.model.Schedule;
import com.fptu.fevent.repository.NotificationRepository;
import com.fptu.fevent.repository.TaskRepository;
import com.fptu.fevent.repository.UserRepository;
import com.fptu.fevent.repository.ScheduleRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationService {
    private static final String TAG = "NotificationService";
    
    private final NotificationRepository notificationRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final PushNotificationManager pushNotificationManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public NotificationService(Application application) {
        this.notificationRepository = new NotificationRepository(application);
        this.taskRepository = new TaskRepository(application);
        this.userRepository = new UserRepository(application);
        this.scheduleRepository = new ScheduleRepository(application);
        this.pushNotificationManager = new PushNotificationManager(application);
    }

    public void checkTaskDelaysAndNotify() {
        executor.execute(() -> {
            try {
                Log.d(TAG, "Checking task delays...");
                
                List<Task> allTasks = taskRepository.getAll();
                Date currentDate = new Date();
                
                for (Task task : allTasks) {
                    checkTaskDelay(task, currentDate);
                    checkTaskDeadline1Hour(task, currentDate);
                }
                
                Log.d(TAG, "Task delay check completed");
            } catch (Exception e) {
                Log.e(TAG, "Error checking task delays", e);
            }
        });
    }

    public void checkScheduleReminders() {
        executor.execute(() -> {
            try {
                Log.d(TAG, "Checking schedule reminders...");
                
                List<Schedule> allSchedules = scheduleRepository.getAll();
                Date currentDate = new Date();
                
                for (Schedule schedule : allSchedules) {
                    checkScheduleReminder1Hour(schedule, currentDate);
                }
                
                Log.d(TAG, "Schedule reminder check completed");
            } catch (Exception e) {
                Log.e(TAG, "Error checking schedule reminders", e);
            }
        });
    }

    private void checkTaskDelay(Task task, Date currentDate) {
        if (task.due_date == null) return;
        
        String normalizedStatus = normalizeStatus(task.status);
        if ("completed".equals(normalizedStatus)) return;

        // Calculate days until due date
        long diffInMs = task.due_date.getTime() - currentDate.getTime();
        long daysUntilDue = diffInMs / (24 * 60 * 60 * 1000);

        // Check for overdue tasks
        if (daysUntilDue < 0) {
            handleOverdueTask(task, Math.abs(daysUntilDue));
        }
        // Check for tasks due soon (1-2 days)
        else if (daysUntilDue <= 2 && daysUntilDue >= 0) {
            handleUpcomingDueTask(task, daysUntilDue);
        }
        // Check for tasks that should have started but haven't
        else if ("not_started".equals(normalizedStatus)) {
            handleNotStartedTask(task, daysUntilDue);
        }
    }

    private void checkTaskDeadline1Hour(Task task, Date currentDate) {
        if (task.due_date == null) return;
        
        String normalizedStatus = normalizeStatus(task.status);
        if ("completed".equals(normalizedStatus)) return;

        // Check if task deadline is within 1 hour
        long diffInMs = task.due_date.getTime() - currentDate.getTime();
        long hoursUntilDue = diffInMs / (60 * 60 * 1000);

        if (hoursUntilDue <= 1 && hoursUntilDue >= 0) {
            handleTaskDeadline1Hour(task);
        }
    }

    private void checkScheduleReminder1Hour(Schedule schedule, Date currentDate) {
        if (schedule.start_time == null) return;

        // Check if schedule starts within 1 hour
        long diffInMs = schedule.start_time.getTime() - currentDate.getTime();
        long hoursUntilStart = diffInMs / (60 * 60 * 1000);

        if (hoursUntilStart <= 1 && hoursUntilStart >= 0) {
            handleScheduleReminder1Hour(schedule);
        }
    }

    private void handleTaskDeadline1Hour(Task task) {
        // Notify assigned user/team
        notifyTaskAssignees(task, "TASK_DEADLINE_1H", "URGENT", 
            "Công việc sắp hết hạn trong 1 giờ", 
            String.format("Công việc '%s' sẽ hết hạn trong vòng 1 giờ tới. Hãy hoàn thành ngay!", 
                task.title));

        // Notify supervisors
        notifySupervisors(task, "TASK_DEADLINE_1H", "URGENT",
            "Công việc sắp hết hạn trong 1 giờ",
            String.format("Công việc '%s' sẽ hết hạn trong vòng 1 giờ tới và cần được theo dõi sát sao.", 
                task.title));
    }

    private void handleScheduleReminder1Hour(Schedule schedule) {
        // Get all users in the team
        if (schedule.team_id != null) {
            List<User> teamMembers = userRepository.getAll().stream()
                .filter(user -> user.team_id != null && user.team_id.equals(schedule.team_id))
                .collect(java.util.stream.Collectors.toList());

            for (User member : teamMembers) {
                // Check for duplicate notifications
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR, -2); // Check notifications in last 2 hours
                Date twoHoursAgo = cal.getTime();
                
                List<Notification> existingNotifications = notificationRepository
                    .getRecentScheduleNotifications(member.id, "SCHEDULE_REMINDER_1H", schedule.id, twoHoursAgo);
                
                if (existingNotifications.isEmpty()) {
                    Notification notification = new Notification(member.id, schedule.id, 
                        "Cuộc họp sắp bắt đầu trong 1 giờ", 
                        String.format("Cuộc họp '%s' sẽ bắt đầu trong vòng 1 giờ tới tại %s. Hãy chuẩn bị sẵn sàng!", 
                            schedule.title, schedule.location), 
                        "SCHEDULE_REMINDER_1H", "HIGH", true);
                    insertNotificationWithPush(notification);
                    Log.d(TAG, String.format("Schedule 1-hour reminder sent to team member %d for schedule %d", member.id, schedule.id));
                }
            }
            
            // Also notify all users with medium priority for team meetings
            List<User> allUsers = userRepository.getAll();
            for (User user : allUsers) {
                // Check for duplicate notifications
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR, -2);
                Date twoHoursAgo = cal.getTime();
                
                List<Notification> existingNotifications = notificationRepository
                    .getRecentScheduleNotifications(user.id, "SCHEDULE_REMINDER_1H", schedule.id, twoHoursAgo);
                
                if (existingNotifications.isEmpty()) {
                    Notification notification = new Notification(user.id, schedule.id, 
                        "Cuộc họp sắp bắt đầu trong 1 giờ", 
                        String.format("Cuộc họp '%s' sẽ bắt đầu trong vòng 1 giờ tới tại %s.", 
                            schedule.title, schedule.location), 
                        "SCHEDULE_REMINDER_1H", "MEDIUM", true);
                    insertNotificationWithPush(notification);
                    Log.d(TAG, String.format("Schedule 1-hour reminder sent to user %d for schedule %d", user.id, schedule.id));
                }
            }
        } else {
            // For general meetings (team_id is null), notify all users with high priority
            List<User> allUsers = userRepository.getAll();
            for (User user : allUsers) {
                // Check for duplicate notifications
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR, -2);
                Date twoHoursAgo = cal.getTime();
                
                List<Notification> existingNotifications = notificationRepository
                    .getRecentScheduleNotifications(user.id, "SCHEDULE_REMINDER_1H", schedule.id, twoHoursAgo);
                
                if (existingNotifications.isEmpty()) {
                    Notification notification = new Notification(user.id, schedule.id, 
                        "Cuộc họp tổng sắp bắt đầu trong 1 giờ", 
                        String.format("Cuộc họp '%s' sẽ bắt đầu trong vòng 1 giờ tới tại %s. Đây là cuộc họp quan trọng dành cho tất cả thành viên!", 
                            schedule.title, schedule.location), 
                        "SCHEDULE_REMINDER_1H", "HIGH", true);
                    insertNotificationWithPush(notification);
                    Log.d(TAG, String.format("General schedule 1-hour reminder sent to user %d for schedule %d", user.id, schedule.id));
                }
            }
        }
    }

    public void notifyTaskAssignment(Task task) {
        executor.execute(() -> {
            // Notify assigned user
            if (task.assigned_to != null) {
                Notification notification = new Notification(task.assigned_to, task.id, 
                    "Bạn được giao công việc mới", 
                    String.format("Bạn đã được giao công việc '%s'. Hạn hoàn thành: %s. Hãy bắt đầu thực hiện ngay!", 
                        task.title, formatDate(task.due_date)), 
                    "TASK_ASSIGNED", "HIGH");
                insertNotificationWithPush(notification);
                Log.d(TAG, String.format("Task assignment notification sent to user %d for task %d", task.assigned_to, task.id));
            }

            // Notify team members if task is assigned to a team
            if (task.team_id != null) {
                List<User> teamMembers = userRepository.getAll().stream()
                    .filter(user -> user.team_id != null && user.team_id.equals(task.team_id))
                    .collect(java.util.stream.Collectors.toList());

                for (User member : teamMembers) {
                    Notification notification = new Notification(member.id, task.id, 
                        "Team của bạn được giao công việc mới", 
                        String.format("Team của bạn đã được giao công việc '%s'. Hạn hoàn thành: %s. Hãy phối hợp để hoàn thành!", 
                            task.title, formatDate(task.due_date)), 
                        "TASK_ASSIGNED", "MEDIUM");
                    insertNotificationWithPush(notification);
                    Log.d(TAG, String.format("Task assignment notification sent to team member %d for task %d", member.id, task.id));
                }
            }
        });
    }

    public void notifyScheduleCreated(Schedule schedule, int createdByUserId) {
        executor.execute(() -> {
            // Get creator info
            User creator = userRepository.getUserById(createdByUserId);
            String creatorName = creator != null ? creator.fullname : "Quản lý";

            // Notify team members if schedule is for a specific team
            if (schedule.team_id != null) {
                List<User> teamMembers = userRepository.getAll().stream()
                    .filter(user -> user.team_id != null && user.team_id.equals(schedule.team_id))
                    .collect(java.util.stream.Collectors.toList());

                for (User member : teamMembers) {
                    if (member.id != createdByUserId) {
                        Notification notification = new Notification(member.id, schedule.id, 
                            "Lịch họp mới được tạo", 
                            String.format("%s đã tạo lịch họp '%s' vào %s tại %s. Hãy sắp xếp thời gian tham gia!", 
                                creatorName, schedule.title, formatDate(schedule.start_time), schedule.location), 
                            "SCHEDULE_CREATED", "MEDIUM", true);
                        insertNotificationWithPush(notification);
                        Log.d(TAG, String.format("Schedule creation notification sent to team member %d for schedule %d", member.id, schedule.id));
                    }
                }
            } else {
                // Notify all users for general meetings
                List<User> allUsers = userRepository.getAll();
                for (User user : allUsers) {
                    if (user.id != createdByUserId) { // Don't notify the creator
                        Notification notification = new Notification(user.id, schedule.id, 
                            "Lịch họp mới được tạo", 
                            String.format("%s đã tạo lịch họp '%s' vào %s tại %s.", 
                                creatorName, schedule.title, formatDate(schedule.start_time), schedule.location), 
                            "SCHEDULE_CREATED", "LOW", true);
                        insertNotificationWithPush(notification);
                        Log.d(TAG, String.format("Schedule creation notification sent to user %d for schedule %d", user.id, schedule.id));
                    }
                }
            }
        });
    }

    private void handleOverdueTask(Task task, long daysOverdue) {
        // Notify assigned user/team
        notifyTaskAssignees(task, "TASK_OVERDUE", "HIGH", 
            "Công việc quá hạn", 
            String.format("Công việc '%s' đã quá hạn %d ngày. Vui lòng hoàn thành ngay.", 
                task.title, daysOverdue));

        // Notify supervisors (Admin, Leaders)
        notifySupervisors(task, "TASK_OVERDUE", "URGENT",
            "Công việc quá hạn cần xử lý",
            String.format("Công việc '%s' đã quá hạn %d ngày và cần được xử lý ngay.", 
                task.title, daysOverdue));
    }

    private void handleUpcomingDueTask(Task task, long daysUntilDue) {
        String priority = daysUntilDue == 0 ? "HIGH" : "MEDIUM";
        String timeText = daysUntilDue == 0 ? "hôm nay" : daysUntilDue + " ngày nữa";
        
        notifyTaskAssignees(task, "TASK_REMINDER", priority,
            "Nhắc nhở công việc sắp hết hạn",
            String.format("Công việc '%s' sẽ hết hạn %s. Vui lòng hoàn thành đúng thời hạn.", 
                task.title, timeText));
    }

    private void handleNotStartedTask(Task task, long daysUntilDue) {
        // Only notify if task should have started (less than 50% time remaining)
        if (daysUntilDue <= 7) { // Tasks due within a week that haven't started
            notifyTaskAssignees(task, "TASK_DELAY", "MEDIUM",
                "Công việc chưa bắt đầu",
                String.format("Công việc '%s' chưa được bắt đầu và sẽ hết hạn trong %d ngày.", 
                    task.title, daysUntilDue));

            // Notify supervisors for critical delays
            if (daysUntilDue <= 3) {
                notifySupervisors(task, "TASK_DELAY", "HIGH",
                    "Công việc chậm tiến độ",
                    String.format("Công việc '%s' chưa bắt đầu và chỉ còn %d ngày đến hạn.", 
                        task.title, daysUntilDue));
            }
        }
    }

    private void notifyTaskAssignees(Task task, String type, String priority, String title, String message) {
        // Check if notification already exists to avoid duplicates
        if (task.assigned_to != null) {
            List<Notification> existingNotifications = notificationRepository
                .getNotificationsByTaskAndType(task.id, type, task.assigned_to);
            
            if (existingNotifications.isEmpty() || shouldCreateNewNotification(existingNotifications)) {
                Notification notification = new Notification(task.assigned_to, task.id, title, message, type, priority);
                insertNotificationWithPush(notification);
                Log.d(TAG, String.format("Notification sent to user %d for task %d", task.assigned_to, task.id));
            }
        }

        // Notify team members if task is assigned to a team
        if (task.team_id != null) {
            List<User> teamMembers = userRepository.getAll().stream()
                .filter(user -> user.team_id != null && user.team_id.equals(task.team_id))
                .collect(java.util.stream.Collectors.toList());

            for (User member : teamMembers) {
                List<Notification> existingNotifications = notificationRepository
                    .getNotificationsByTaskAndType(task.id, type, member.id);
                
                if (existingNotifications.isEmpty() || shouldCreateNewNotification(existingNotifications)) {
                    Notification notification = new Notification(member.id, task.id, title, message, type, priority);
                    insertNotificationWithPush(notification);
                    Log.d(TAG, String.format("Notification sent to team member %d for task %d", member.id, task.id));
                }
            }
        }
    }

    private void notifySupervisors(Task task, String type, String priority, String title, String message) {
        List<User> supervisors = userRepository.getAll().stream()
            .filter(user -> user.role_id != null && (user.role_id == 1 || user.role_id == 2 || user.role_id == 3))
            .collect(java.util.stream.Collectors.toList());

        for (User supervisor : supervisors) {
            List<Notification> existingNotifications = notificationRepository
                .getNotificationsByTaskAndType(task.id, type, supervisor.id);
            
            if (existingNotifications.isEmpty() || shouldCreateNewNotification(existingNotifications)) {
                Notification notification = new Notification(supervisor.id, task.id, title, message, type, priority);
                insertNotificationWithPush(notification);
                Log.d(TAG, String.format("Notification sent to supervisor %d for task %d", supervisor.id, task.id));
            }
        }
    }

    private boolean shouldCreateNewNotification(List<Notification> existingNotifications) {
        if (existingNotifications.isEmpty()) return true;
        
        // Check if the last notification was sent more than 24 hours ago
        Notification lastNotification = existingNotifications.get(0);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date oneDayAgo = cal.getTime();
        
        return lastNotification.created_at.before(oneDayAgo);
    }

    private String normalizeStatus(String status) {
        if (status == null) return "not_started";
        
        switch (status.toLowerCase()) {
            case "completed":
            case "hoàn thành":
                return "completed";
            case "in_progress":
            case "đang thực hiện":
                return "in_progress";
            case "overdue":
            case "quá hạn":
                return "overdue";
            default:
                return "not_started";
        }
    }

    private String formatDate(Date date) {
        if (date == null) return "Chưa xác định";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        return sdf.format(date);
    }

    public void createCustomNotification(int userId, String title, String message, String type, String priority) {
        Notification notification = new Notification(userId, title, message, type, priority);
        insertNotificationWithPush(notification);
    }

    public void cleanupOldNotifications() {
        executor.execute(() -> {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -30); // Delete notifications older than 30 days
            Date cutoffDate = cal.getTime();
            
            notificationRepository.deleteOldNotifications(cutoffDate);
            Log.d(TAG, "Old notifications cleaned up");
        });
    }

    /**
     * Helper method để vừa insert notification vào database vừa hiển thị push notification
     */
    private void insertNotificationWithPush(Notification notification) {
        // Insert vào database
        notificationRepository.insert(notification);
        
        // Hiển thị push notification trên màn hình điện thoại
        pushNotificationManager.showPushNotification(notification);
        
        Log.d(TAG, String.format("Notification created and pushed: %s for user %d", 
                notification.title, notification.user_id));
    }
} 