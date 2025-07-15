package com.fptu.fevent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.model.Notification;
import com.fptu.fevent.service.PushNotificationManager;
import com.fptu.fevent.ui.common.HomeActivity;
import com.fptu.fevent.util.NotificationPermissionHelper;

public class MainActivity extends AppCompatActivity {

    private PushNotificationManager pushNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Initialize push notification manager
        pushNotificationManager = new PushNotificationManager(this);
        
        // Request notification permission
        NotificationPermissionHelper.requestNotificationPermission(this);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        setupButtons();
    }
    
    private void setupButtons() {
        Button btnTestTaskAssigned = findViewById(R.id.btnTestTaskAssigned);
        Button btnTestTaskDeadline = findViewById(R.id.btnTestTaskDeadline);
        Button btnTestScheduleCreated = findViewById(R.id.btnTestScheduleCreated);
        Button btnTestScheduleReminder = findViewById(R.id.btnTestScheduleReminder);
        Button btnTestTaskOverdue = findViewById(R.id.btnTestTaskOverdue);
        Button btnGoToHome = findViewById(R.id.btnGoToHome);
        
        btnTestTaskAssigned.setOnClickListener(v -> testTaskAssignedNotification());
        btnTestTaskDeadline.setOnClickListener(v -> testTaskDeadlineNotification());
        btnTestScheduleCreated.setOnClickListener(v -> testScheduleCreatedNotification());
        btnTestScheduleReminder.setOnClickListener(v -> testScheduleReminderNotification());
        btnTestTaskOverdue.setOnClickListener(v -> testTaskOverdueNotification());
        btnGoToHome.setOnClickListener(v -> goToHome());
    }
    
    private void testTaskAssignedNotification() {
        Notification notification = new Notification(
            1, // user_id
            101, // task_id
            "📝 Bạn được giao công việc mới!",
            "Công việc 'Chuẩn bị báo cáo tháng' đã được giao cho bạn. Hạn: 31/12/2024",
            "TASK_ASSIGNED",
            "HIGH"
        );
        notification.id = 1001;
        
        pushNotificationManager.showPushNotification(notification);
        Toast.makeText(this, "Đã gửi thông báo giao việc!", Toast.LENGTH_SHORT).show();
    }
    
    private void testTaskDeadlineNotification() {
        Notification notification = new Notification(
            1, // user_id  
            102, // task_id
            "⏰ Công việc sắp hết hạn!",
            "Công việc 'Hoàn thiện presentation' sẽ hết hạn trong 1 giờ nữa. Hãy nhanh chóng hoàn thành!",
            "TASK_DEADLINE_1H",
            "URGENT"
        );
        notification.id = 1002;
        
        pushNotificationManager.showPushNotification(notification);
        Toast.makeText(this, "Đã gửi thông báo deadline 1h!", Toast.LENGTH_SHORT).show();
    }
    
    private void testScheduleCreatedNotification() {
        Notification notification = new Notification(
            1, // user_id
            201, // schedule_id  
            "📅 Lịch họp mới được tạo",
            "Admin vừa tạo cuộc họp 'Họp team hàng tuần' vào 31/12/2024 14:00 tại Phòng họp A101",
            "SCHEDULE_CREATED",
            "MEDIUM",
            true // isSchedule = true
        );
        notification.id = 1003;
        
        pushNotificationManager.showPushNotification(notification);
        Toast.makeText(this, "Đã gửi thông báo lịch họp mới!", Toast.LENGTH_SHORT).show();
    }
    
    private void testScheduleReminderNotification() {
        Notification notification = new Notification(
            1, // user_id
            202, // schedule_id
            "🔔 Cuộc họp sắp bắt đầu!",
            "Cuộc họp 'Daily standup' sẽ bắt đầu trong 1 giờ nữa tại Phòng B202. Hãy chuẩn bị sẵn sàng!",
            "SCHEDULE_REMINDER_1H", 
            "HIGH",
            true // isSchedule = true
        );
        notification.id = 1004;
        
        pushNotificationManager.showPushNotification(notification);
        Toast.makeText(this, "Đã gửi nhắc lịch họp 1h!", Toast.LENGTH_SHORT).show();
    }
    
    private void testTaskOverdueNotification() {
        Notification notification = new Notification(
            1, // user_id
            103, // task_id
            "🚨 Công việc đã quá hạn!",
            "Công việc 'Review code module login' đã quá hạn 2 ngày. Vui lòng hoàn thành ngay!",
            "TASK_OVERDUE",
            "URGENT"
        );
        notification.id = 1005;
        
        pushNotificationManager.showPushNotification(notification);
        Toast.makeText(this, "Đã gửi thông báo quá hạn!", Toast.LENGTH_SHORT).show();
    }
    
    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        NotificationPermissionHelper.handlePermissionResult(this, requestCode, permissions, grantResults);
    }
}