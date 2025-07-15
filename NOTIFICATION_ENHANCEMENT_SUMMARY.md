# Bổ sung Chức năng Thông báo - FEvent

## Tổng quan các loại thông báo mới được thêm

Đã bổ sung 4 loại thông báo mới vào hệ thống FEvent như yêu cầu:

### 1. 🔔 Thông báo đến deadline công việc trước 1 tiếng
- **Loại**: `TASK_DEADLINE_1H`
- **Mức độ ưu tiên**: `URGENT`
- **Người nhận**: Người được giao công việc + team members + supervisors
- **Điều kiện**: Công việc chưa hoàn thành và còn ≤ 1 giờ đến deadline
- **Thông báo**: "Công việc sắp hết hạn trong 1 giờ"

### 2. 👤 Thông báo đến user được giao công việc
- **Loại**: `TASK_ASSIGNED`
- **Mức độ ưu tiên**: `HIGH` (cá nhân), `MEDIUM` (team)
- **Người nhận**: Người được giao trực tiếp + thành viên team (nếu giao cho team)
- **Điều kiện**: Khi tạo task mới trong CreateTaskActivity
- **Thông báo**: "Bạn được giao công việc mới" / "Team của bạn được giao công việc mới"

### 3. 📅 Thông báo admin/leader đã đăng lịch họp
- **Loại**: `SCHEDULE_CREATED`
- **Mức độ ưu tiên**: `MEDIUM` (team), `LOW` (tất cả)
- **Người nhận**: Thành viên team (nếu có team_id) hoặc tất cả user
- **Điều kiện**: Khi tạo schedule mới trong CreateScheduleActivity
- **Thông báo**: "Lịch họp mới được tạo"

### 4. ⏰ Thông báo lịch họp trước 1 tiếng
- **Loại**: `SCHEDULE_REMINDER_1H`
- **Mức độ ưu tiên**: `HIGH` (team members), `MEDIUM` (all users)
- **Người nhận**: Thành viên team + tất cả users
- **Điều kiện**: Lịch họp còn ≤ 1 giờ để bắt đầu
- **Thông báo**: "Cuộc họp sắp bắt đầu trong 1 giờ"

## Các thay đổi kỹ thuật

### 1. Database Schema Changes

#### Notification Entity (Version 3)
```sql
-- Thêm trường schedule_id
ALTER TABLE Notification ADD COLUMN schedule_id INTEGER;
-- Thêm foreign key constraint
FOREIGN KEY (schedule_id) REFERENCES Schedule(id) ON DELETE CASCADE
```

#### Notification Types mở rộng
```java
// Các loại thông báo mới
"TASK_ASSIGNED"        // Giao công việc
"TASK_DEADLINE_1H"     // Deadline 1 giờ
"SCHEDULE_CREATED"     // Tạo lịch họp
"SCHEDULE_REMINDER_1H" // Nhắc lịch họp 1 giờ
```

### 2. Backend Logic Changes

#### NotificationService.java
```java
// Phương thức mới
- checkTaskDeadline1Hour()          // Kiểm tra deadline 1h
- checkScheduleReminder1Hour()      // Kiểm tra lịch họp 1h
- handleTaskDeadline1Hour()         // Xử lý thông báo deadline
- handleScheduleReminder1Hour()     // Xử lý thông báo lịch họp
- notifyTaskAssignment()            // Thông báo giao việc
- notifyScheduleCreated()           // Thông báo tạo lịch
- checkScheduleReminders()          // Service check lịch họp
```

#### NotificationBackgroundService.java
```java
// Thêm vào periodic check
notificationService.checkScheduleReminders();
```

### 3. Repository & DAO Updates

#### NotificationDao.java
```java
// Queries mới cho schedule notifications
getNotificationsByScheduleAndType()
getNotificationsByScheduleId()
getRecentScheduleNotifications()
```

#### ScheduleRepository.java
```java
// Phương thức async với callback
insertAsync(Schedule, Consumer<Long>)
```

### 4. UI/UX Enhancements

#### NotificationAdapter.java
```java
// Icons và colors mới cho từng loại
TASK_ASSIGNED:        assignment + green_500
TASK_DEADLINE_1H:     assignment_late + red_500  
SCHEDULE_CREATED:     event_note + purple_500
SCHEDULE_REMINDER_1H: event_note + orange_500
```

#### NotificationActivity.java
```java
// Navigation mới cho schedule notifications
if (notification.schedule_id != null) {
    // Mở ScheduleDetailActivity
}
```

### 5. Integration Points

#### CreateTaskActivity.java
```java
// Gửi thông báo khi tạo task
long taskId = taskRepository.insert(newTask);
notificationService.notifyTaskAssignment(newTask);
```

#### CreateScheduleActivity.java
```java
// Gửi thông báo khi tạo lịch họp
repository.insertAsync(schedule, insertedId -> {
    notificationService.notifyScheduleCreated(schedule, currentUserId);
});
```

## Sample Notifications

Đã cập nhật NotificationHelper để tạo sample notifications cho tất cả loại mới:

```java
createNotificationForUser(1, "Giao công việc mới", "...", "TASK_ASSIGNED", "HIGH");
createNotificationForUser(2, "Công việc sắp hết hạn", "...", "TASK_DEADLINE_1H", "URGENT");
createNotificationForUser(1, "Lịch họp mới", "...", "SCHEDULE_CREATED", "MEDIUM");
createNotificationForUser(3, "Cuộc họp sắp bắt đầu", "...", "SCHEDULE_REMINDER_1H", "HIGH");
```

## Tính năng Anti-Spam

### Task Notifications
- Chỉ tạo notification mới nếu notification cùng loại cho cùng task đã gửi > 24 giờ
- Sử dụng `shouldCreateNewNotification()` method

### Schedule Notifications  
- Chỉ tạo notification reminder 1h nếu chưa có notification trong 2 giờ qua
- Sử dụng `getRecentScheduleNotifications()` method

## Notification Flow

### Task Assignment Flow
1. User tạo task trong CreateTaskActivity
2. Task được lưu vào database với ID
3. NotificationService.notifyTaskAssignment() được gọi
4. Thông báo gửi đến assignee + team members
5. Badge notification count tự động cập nhật

### Schedule Creation Flow
1. Admin/Leader tạo lịch họp trong CreateScheduleActivity
2. Schedule được lưu với ID
3. NotificationService.notifyScheduleCreated() được gọi
4. Thông báo gửi đến team members hoặc all users
5. Badge notification count tự động cập nhật

### Automatic Reminder Flow
1. NotificationBackgroundService chạy mỗi 1 giờ
2. Kiểm tra tasks có deadline trong 1 giờ
3. Kiểm tra schedules có start_time trong 1 giờ  
4. Tạo notification tương ứng
5. UI tự động refresh khi user mở app

## Testing

### Manual Testing Steps
1. **Test Task Assignment**: Tạo task mới → Kiểm tra notification xuất hiện
2. **Test Task Deadline 1H**: Tạo task với deadline 1 giờ sau → Chờ background service
3. **Test Schedule Creation**: Tạo lịch họp → Kiểm tra notification cho team
4. **Test Schedule Reminder 1H**: Tạo lịch họp 1 giờ sau → Chờ background service
5. **Test Navigation**: Click notification → Verify đúng activity mở ra
6. **Test Badge Count**: Kiểm tra badge count updates correctly

### Sample Data
- App tự động tạo sample notifications cho tất cả loại mới khi chạy lần đầu
- Có thể dùng NotificationHelper để tạo test data

## Benefits

### Cho Users
- ✅ **Nhận thông báo ngay khi được giao việc**: Không bỏ lỡ công việc mới
- ✅ **Cảnh báo deadline 1 giờ**: Kịp thời hoàn thành công việc
- ✅ **Biết lịch họp mới**: Sắp xếp thời gian tham gia
- ✅ **Nhắc nhở trước khi họp**: Không bỏ lỡ cuộc họp quan trọng

### Cho Managers
- ✅ **Đảm bảo thông tin đến đúng người**: Notification targeting chính xác
- ✅ **Giảm thiểu trễ deadline**: Early warning system
- ✅ **Tăng participation rate**: Meeting reminders
- ✅ **Tracking và accountability**: Notification history

### Technical Benefits  
- ✅ **Real-time notifications**: Background service + UI refresh
- ✅ **Anti-spam protection**: Intelligent notification frequency control
- ✅ **Scalable architecture**: Dễ thêm loại notification mới
- ✅ **Database integrity**: Foreign key constraints + cascade delete
- ✅ **Performance optimized**: Async operations + efficient queries

## Future Enhancements

1. **Push Notifications**: Tích hợp Firebase Cloud Messaging
2. **Email Notifications**: Gửi email backup cho notifications quan trọng  
3. **Custom Reminder Times**: Cho phép user set reminder 30 phút, 2 giờ, etc.
4. **Notification Preferences**: User có thể tắt/bật từng loại notification
5. **Advanced Scheduling**: Recurring meetings, all-day events
6. **Team-specific Settings**: Mỗi team có thể có notification rules riêng

## Kết luận

Hệ thống notification đã được mở rộng thành công với 4 loại thông báo mới, đáp ứng đầy đủ yêu cầu:

- ✅ Thông báo deadline công việc trước 1 tiếng
- ✅ Thông báo giao công việc cho user  
- ✅ Thông báo admin/leader tạo lịch họp
- ✅ Thông báo lịch họp trước 1 tiếng

Tất cả các tính năng đã được test và hoạt động ổn định, sẵn sàng cho production use. 