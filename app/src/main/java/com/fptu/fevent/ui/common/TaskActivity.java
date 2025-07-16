package com.fptu.fevent.ui.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.Task;
import com.fptu.fevent.repository.TaskRepository;
import com.fptu.fevent.ui.task.CreateTaskActivity;
import com.fptu.fevent.ui.task.TaskAdapter;
import com.fptu.fevent.ui.task.TaskSummaryActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private FloatingActionButton fabCreateTask;
    private MaterialButton btnTaskSummary;
    private TaskRepository taskRepository;
    private List<Task> taskList;
    private ExecutorService executor;
    private int currentUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task);
        
        // Khởi tạo
        taskRepository = new TaskRepository(getApplication());
        taskList = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();
        
        // Lấy role của user hiện tại
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserRole = prefs.getInt("role_id", 4); // Default là Member
        
        initViews();
        setupRecyclerView();
        setupFAB();
        setupSummaryButton();
        loadTasks();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewTasks);
        fabCreateTask = findViewById(R.id.fabCreateTask);
        btnTaskSummary = findViewById(R.id.btnTaskSummary);
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(taskList, this::onTaskClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);
    }

    private void setupFAB() {
        // Chỉ hiển thị nút tạo công việc cho admin (1), leader (3), leader_ban (2)
        if (currentUserRole == 1 || currentUserRole == 2 || currentUserRole == 3) {
            fabCreateTask.setVisibility(View.VISIBLE);
            fabCreateTask.setOnClickListener(v -> {
                Intent intent = new Intent(TaskActivity.this, CreateTaskActivity.class);
                startActivity(intent);
            });
        } else {
            fabCreateTask.setVisibility(View.GONE);
        }
    }

    private void setupSummaryButton() {
        // Chỉ hiển thị nút tổng hợp cho admin (1), leader (3), leader_ban (2)
        if (currentUserRole == 1 || currentUserRole == 2 || currentUserRole == 3) {
            btnTaskSummary.setVisibility(View.VISIBLE);
            btnTaskSummary.setOnClickListener(v -> {
                Intent intent = new Intent(TaskActivity.this, TaskSummaryActivity.class);
                startActivity(intent);
            });
        } else {
            btnTaskSummary.setVisibility(View.GONE);
        }
    }

    private void loadTasks() {
        executor.execute(() -> {
            List<Task> tasks = taskRepository.getAll();
            runOnUiThread(() -> {
                taskList.clear();
                taskList.addAll(tasks);
                taskAdapter.notifyDataSetChanged();
                
                // Show/hide empty state
                View emptyState = findViewById(R.id.emptyState);
                if (tasks.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void onTaskClick(Task task) {
        // Mở TaskDetailActivity để xem chi tiết task
        Intent intent = new Intent(TaskActivity.this, com.fptu.fevent.ui.task.TaskDetailActivity.class);
        intent.putExtra("task_id", task.id);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh danh sách khi quay lại từ CreateTaskActivity
        loadTasks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}