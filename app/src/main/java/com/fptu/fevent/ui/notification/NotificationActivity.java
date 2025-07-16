package com.fptu.fevent.ui.notification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.Notification;
import com.fptu.fevent.repository.NotificationRepository;
import com.fptu.fevent.service.NotificationService;
import com.fptu.fevent.ui.task.TaskDetailActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class NotificationActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationClickListener {

    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private MaterialButton btnBack, btnMarkAllRead;
    private MaterialButton btnFilterAll, btnFilterUnread, btnFilterUrgent;
    private View emptyState;
    
    private NotificationRepository notificationRepository;
    private NotificationService notificationService;
    private ExecutorService executor;
    
    private List<Notification> allNotifications;
    private List<Notification> filteredNotifications;
    private int currentUserId;
    private String currentFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        
        // Initialize repositories and services
        notificationRepository = new NotificationRepository(getApplication());
        notificationService = new NotificationService(getApplication());
        executor = Executors.newSingleThreadExecutor();
        
        // Get current user
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);
        
        // Initialize data lists
        allNotifications = new ArrayList<>();
        filteredNotifications = new ArrayList<>();
        
        initViews();
        setupRecyclerView();
        setupButtons();
        
        // Check for new notifications and load data
        checkForNewNotifications();
        loadNotifications();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        btnBack = findViewById(R.id.btnBack);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterUnread = findViewById(R.id.btnFilterUnread);
        btnFilterUrgent = findViewById(R.id.btnFilterUrgent);
        emptyState = findViewById(R.id.emptyState);
    }

    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter(filteredNotifications, this);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotifications.setAdapter(notificationAdapter);
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());
        
        btnMarkAllRead.setOnClickListener(v -> markAllAsRead());
        
        btnFilterAll.setOnClickListener(v -> setFilter("ALL"));
        btnFilterUnread.setOnClickListener(v -> setFilter("UNREAD"));
        btnFilterUrgent.setOnClickListener(v -> setFilter("URGENT"));
    }

    private void checkForNewNotifications() {
        // Trigger notification service to check for new task delays
        notificationService.checkTaskDelaysAndNotify();
    }

    private void loadNotifications() {
        executor.execute(() -> {
            List<Notification> notifications = notificationRepository.getNotificationsByUserId(currentUserId);
            
            runOnUiThread(() -> {
                allNotifications.clear();
                allNotifications.addAll(notifications);
                applyFilter();
                updateEmptyState();
            });
        });
    }

    private void setFilter(String filter) {
        currentFilter = filter;
        updateFilterButtons();
        applyFilter();
    }

    private void updateFilterButtons() {
        // Reset all buttons
        resetFilterButton(btnFilterAll);
        resetFilterButton(btnFilterUnread);
        resetFilterButton(btnFilterUrgent);
        
        // Highlight selected button
        MaterialButton selectedButton;
        switch (currentFilter) {
            case "UNREAD":
                selectedButton = btnFilterUnread;
                break;
            case "URGENT":
                selectedButton = btnFilterUrgent;
                break;
            default:
                selectedButton = btnFilterAll;
                break;
        }
        
        selectedButton.setBackgroundColor(getResources().getColor(R.color.blue_500));
        selectedButton.setTextColor(getResources().getColor(R.color.white));
    }

    private void resetFilterButton(MaterialButton button) {
        button.setBackgroundColor(getResources().getColor(R.color.gray_200));
        button.setTextColor(getResources().getColor(R.color.gray_700));
    }

    private void applyFilter() {
        filteredNotifications.clear();
        
        switch (currentFilter) {
            case "UNREAD":
                filteredNotifications.addAll(allNotifications.stream()
                    .filter(n -> !n.is_read)
                    .collect(Collectors.toList()));
                break;
            case "URGENT":
                filteredNotifications.addAll(allNotifications.stream()
                    .filter(n -> "URGENT".equals(n.priority) || "HIGH".equals(n.priority))
                    .collect(Collectors.toList()));
                break;
            default:
                filteredNotifications.addAll(allNotifications);
                break;
        }
        
        notificationAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredNotifications.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerViewNotifications.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerViewNotifications.setVisibility(View.VISIBLE);
        }
    }

    private void markAllAsRead() {
        executor.execute(() -> {
            notificationRepository.markAllAsRead(currentUserId);
            
            runOnUiThread(() -> {
                // Update local data
                for (Notification notification : allNotifications) {
                    notification.is_read = true;
                }
                
                notificationAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Đã đánh dấu tất cả thông báo là đã đọc", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public void onNotificationClick(Notification notification) {
        // Mark notification as read
        if (!notification.is_read) {
            executor.execute(() -> {
                notificationRepository.markAsRead(notification.id);
                
                runOnUiThread(() -> {
                    notification.is_read = true;
                    notificationAdapter.notifyDataSetChanged();
                });
            });
        }
        
        // Navigate to task detail if it's a task-related notification
        if (notification.task_id != null) {
            Intent intent = new Intent(this, com.fptu.fevent.ui.task.TaskDetailActivity.class);
            intent.putExtra("task_id", notification.task_id);
            startActivity(intent);
        } 
        // Navigate to schedule detail if it's a schedule-related notification
        else if (notification.schedule_id != null) {
            Intent intent = new Intent(this, com.fptu.fevent.ui.common.ScheduleDetailActivity.class);
            intent.putExtra("schedule_id", notification.schedule_id);
            startActivity(intent);
        } 
        else {
            // Show notification details for non-task/schedule notifications
            showNotificationDetails(notification);
        }
    }

    private void showNotificationDetails(Notification notification) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(notification.title)
                .setMessage(notification.message)
                .setPositiveButton("Đóng", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh notifications when returning to activity
        loadNotifications();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
} 