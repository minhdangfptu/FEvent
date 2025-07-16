# Chức năng Thông báo Chậm tiến độ Công việc - FEvent

## Tổng quan

Chức năng thông báo chậm tiến độ công việc được thiết kế để tự động theo dõi và cảnh báo về các công việc bị trễ hạn, chậm tiến độ, hoặc sắp hết hạn trong hệ thống FEvent.

## Các thành phần chính

### 1. Database Schema

#### Bảng Notification
```sql
CREATE TABLE Notification (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    task_id INTEGER, -- nullable cho thông báo không liên quan task
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    type TEXT NOT NULL, -- "TASK_DELAY", "TASK_OVERDUE", "TASK_REMINDER", "GENERAL"
    priority TEXT NOT NULL, -- "LOW", "MEDIUM", "HIGH", "URGENT"
    is_read BOOLEAN DEFAULT 0,
    created_at DATE NOT NULL,
    read_at DATE,
    FOREIGN KEY (user_id) REFERENCES User(id),
    FOREIGN KEY (task_id) REFERENCES Task(id)
);
```

### 2. Core Components

#### NotificationService
- **Chức năng**: Kiểm tra và tạo thông báo tự động
- **Các loại thông báo**:
  - `TASK_OVERDUE`: Công việc quá hạn
  - `TASK_DELAY`: Công việc chậm tiến độ (chưa bắt đầu gần hết hạn)
  - `TASK_REMINDER`: Nhắc nhở công việc sắp hết hạn
  - `GENERAL`: Thông báo chung

#### NotificationRepository
- Quản lý CRUD operations cho thông báo
- Hỗ trợ các query phức tạp (unread count, filter by type/priority)

#### NotificationBackgroundService
- Service chạy nền, kiểm tra định kỳ mỗi 1 giờ
- Tự động khởi động khi app mở
- Tự động restart nếu bị kill

### 3. User Interface

#### NotificationActivity
- Hiển thị danh sách thông báo với filter
- Phân loại: Tất cả, Chưa đọc, Khẩn cấp
- Tính năng đánh dấu đã đọc (từng cái hoặc tất cả)
- Navigation đến TaskDetailActivity khi click thông báo task

#### NotificationAdapter
- RecyclerView adapter với design hiện đại
- Hiển thị priority indicator, type badge, unread status
- Time formatting (vừa xong, X phút trước, X giờ trước, etc.)

#### TopMenuFragment
- Badge thông báo hiển thị số lượng chưa đọc
- Click để mở NotificationActivity
- Auto-refresh khi quay lại

### 4. Notification Logic

#### Điều kiện tạo thông báo

1. **Công việc quá hạn** (TASK_OVERDUE):
   - `due_date < current_date`
   - `status != "Hoàn thành"`
   - Priority: HIGH cho assignee, URGENT cho supervisor

2. **Công việc chậm tiến độ** (TASK_DELAY):
   - `status = "Chưa bắt đầu"`
   - `days_until_due <= 7`
   - Priority: MEDIUM cho assignee, HIGH cho supervisor (nếu <= 3 ngày)

3. **Nhắc nhở công việc** (TASK_REMINDER):
   - `days_until_due <= 2`
   - Priority: HIGH nếu hôm nay, MEDIUM nếu 1-2 ngày

#### Người nhận thông báo

- **Assignee**: Người được giao công việc trực tiếp
- **Team Members**: Thành viên trong team được giao công việc
- **Supervisors**: Admin (role_id=1), Leader (role_id=2,3) cho các trường hợp nghiêm trọng

#### Chống spam thông báo
- Chỉ tạo thông báo mới nếu thông báo cùng loại cho cùng task đã được gửi > 24 giờ
- Tự động xóa thông báo cũ hơn 30 ngày

### 5. Integration Points

#### HomeActivity
- Khởi động NotificationBackgroundService
- Tạo sample notifications (chỉ lần đầu)
- Navigation đến NotificationActivity từ drawer menu

#### TaskDetailActivity
- Có thể nhận task_id từ notification click
- Hiển thị chi tiết công việc liên quan

#### TaskSummaryActivity
- Hiển thị thống kê overdue tasks
- Tích hợp với notification system

## Cách sử dụng

### 1. Xem thông báo
- Click icon thông báo ở header (có badge số lượng chưa đọc)
- Hoặc vào menu drawer → "Thông báo"

### 2. Filter thông báo
- **Tất cả**: Hiển thị tất cả thông báo
- **Chưa đọc**: Chỉ thông báo chưa đọc
- **Khẩn cấp**: Thông báo priority HIGH/URGENT

### 3. Tương tác
- Click thông báo → Auto đánh dấu đã đọc
- Click thông báo task → Mở TaskDetailActivity
- Click "Đánh dấu tất cả" → Đánh dấu tất cả là đã đọc

### 4. Automatic Monitoring
- Service tự động chạy nền
- Kiểm tra mỗi 1 giờ
- Tạo thông báo khi phát hiện delay/overdue

## Technical Features

### 1. Performance Optimization
- Background service với interval hợp lý (1 giờ)
- Async operations với ExecutorService
- Efficient database queries với indexing

### 2. User Experience
- Modern Material Design UI
- Intuitive color coding (priority indicators)
- Real-time badge updates
- Smooth navigation flow

### 3. Scalability
- Configurable notification types
- Extensible priority system
- Role-based notification targeting
- Automatic cleanup mechanism

### 4. Error Handling
- Null safety checks
- Database transaction safety
- Service restart capability
- Graceful degradation

## Files Created/Modified

### New Files
- `Notification.java` - Entity model
- `NotificationDao.java` - Database access
- `NotificationRepository.java` - Data layer
- `NotificationService.java` - Business logic
- `NotificationBackgroundService.java` - Background processing
- `NotificationActivity.java` - Main UI
- `NotificationAdapter.java` - RecyclerView adapter
- `NotificationHelper.java` - Utility functions
- `activity_notification.xml` - Activity layout
- `item_notification.xml` - List item layout
- `bg_notification_type.xml` - Badge background
- `baseline_assignment_late_24.xml` - Icon

### Modified Files
- `AppDatabase.java` - Added Notification entity
- `TopMenuFragment.java` - Added badge functionality
- `HomeActivity.java` - Service startup & navigation
- `AndroidManifest.xml` - Service registration
- `layout_top_buttons.xml` - Badge UI
- `colors.xml` - New colors
- `activity_task.xml` - Show summary button

## Testing

### Sample Data
- Tự động tạo sample notifications khi chạy lần đầu
- Test data cho các loại thông báo khác nhau
- Kiểm tra badge counter và UI states

### Manual Testing
1. Tạo task với due date trong quá khứ → Kiểm tra OVERDUE notification
2. Tạo task với due date 1-2 ngày → Kiểm tra REMINDER notification
3. Tạo task "Chưa bắt đầu" gần hết hạn → Kiểm tra DELAY notification
4. Test filter functionality
5. Test mark as read functionality
6. Test navigation to TaskDetailActivity

## Future Enhancements

1. **Push Notifications**: Tích hợp Firebase Cloud Messaging
2. **Email Notifications**: Gửi email cho thông báo quan trọng
3. **Customizable Settings**: Cho phép user config notification preferences
4. **Advanced Analytics**: Thống kê về performance và delay patterns
5. **Notification Templates**: Template system cho custom messages
6. **Escalation Rules**: Tự động escalate đến cấp cao hơn nếu không response

## Kết luận

Chức năng thông báo chậm tiến độ công việc đã được triển khai hoàn chỉnh với:
- ✅ Tự động monitor và cảnh báo
- ✅ UI/UX hiện đại và trực quan
- ✅ Performance optimization
- ✅ Scalable architecture
- ✅ Comprehensive testing

Hệ thống sẵn sàng để sử dụng và có thể mở rộng thêm các tính năng nâng cao trong tương lai. 