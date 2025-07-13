package com.fptu.fevent.ui.task;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;
import com.fptu.fevent.model.Task;
import com.fptu.fevent.model.Team;
import com.fptu.fevent.model.User;
import com.fptu.fevent.repository.TaskRepository;
import com.fptu.fevent.repository.TeamRepository;
import com.fptu.fevent.repository.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Intent;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvDescription, tvStatus, tvDueDate, tvAssignedTo, tvCreatedDate;
    private View statusIndicator;
    private MaterialButton btnBack, btnEdit, btnUpdateProgress;
    private MaterialCardView cardStatus;
    
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private TeamRepository teamRepository;
    private ExecutorService executor;
    
    private Task currentTask;
    private int currentUserRole;
    private int currentUserId;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_detail);
        
        // Khởi tạo repositories
        taskRepository = new TaskRepository(getApplication());
        userRepository = new UserRepository(getApplication());
        teamRepository = new TeamRepository(getApplication());
        executor = Executors.newSingleThreadExecutor();
        
        // Lấy role của user hiện tại
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserRole = prefs.getInt("role_id", 4);
        currentUserId = prefs.getInt("user_id", -1);
        
        // Lấy task ID từ intent
        taskId = getIntent().getIntExtra("task_id", -1);
        if (taskId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin công việc", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupButtons();
        loadTaskDetails();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvStatus = findViewById(R.id.tvStatus);
        tvDueDate = findViewById(R.id.tvDueDate);
        tvAssignedTo = findViewById(R.id.tvAssignedTo);
        tvCreatedDate = findViewById(R.id.tvCreatedDate);
        statusIndicator = findViewById(R.id.statusIndicator);
        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);
        btnUpdateProgress = findViewById(R.id.btnUpdateProgress);
        cardStatus = findViewById(R.id.cardStatus);
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());
        
        // Chỉ hiển thị nút Edit cho admin, leader, leader_ban
        if (currentUserRole == 1 || currentUserRole == 2 || currentUserRole == 3) {
            btnEdit.setVisibility(View.VISIBLE);
            btnUpdateProgress.setVisibility(View.GONE);
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(TaskDetailActivity.this, EditTaskActivity.class);
                intent.putExtra("task_id", taskId);
                startActivityForResult(intent, 1002);
            });
        } else {
            // Đối với member, chỉ hiển thị nút Update Progress nếu task được giao cho họ
            btnEdit.setVisibility(View.GONE);
            setupUpdateProgressButton();
        }
    }
    
    private void setupUpdateProgressButton() {
        if (currentTask == null) {
            btnUpdateProgress.setVisibility(View.GONE);
            return;
        }
        
        // Kiểm tra xem user có quyền cập nhật task này không
        boolean canUpdate = false;
        if (currentTask.assigned_to != null && currentTask.assigned_to == currentUserId) {
            canUpdate = true;
        } else if (currentTask.team_id != null) {
            // Tạm thời cho phép cập nhật nếu task được giao cho team
            // TODO: Kiểm tra xem user có thuộc team này không trong tương lai
            canUpdate = true;
        }
        
        if (canUpdate) {
            btnUpdateProgress.setVisibility(View.VISIBLE);
            btnUpdateProgress.setOnClickListener(v -> {
                Intent intent = new Intent(TaskDetailActivity.this, UpdateTaskActivity.class);
                intent.putExtra("task_id", taskId);
                startActivityForResult(intent, 1001);
            });
        } else {
            btnUpdateProgress.setVisibility(View.GONE);
        }
    }

    private void loadTaskDetails() {
        executor.execute(() -> {
            Task task = taskRepository.getTaskById(taskId);
            if (task != null) {
                currentTask = task;
                runOnUiThread(() -> displayTaskDetails(task));
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Không tìm thấy công việc", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void displayTaskDetails(Task task) {
        // Hiển thị thông tin cơ bản
        tvTitle.setText(task.title);
        tvDescription.setText(task.description != null ? task.description : "Không có mô tả");
        tvStatus.setText(getStatusText(task.status));
        
        // Hiển thị và set màu cho status
        setStatusColor(task.status);
        
        // Hiển thị ngày hết hạn
        if (task.due_date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvDueDate.setText(sdf.format(task.due_date));
        } else {
            tvDueDate.setText("Không có hạn chót");
        }
        
        // Hiển thị ngày tạo
        tvCreatedDate.setText("Ngày tạo: Không có thông tin");
        
        // Load thông tin người/ban được giao
        loadAssigneeInfo(task);
        
        // Cập nhật button visibility cho members
        if (currentUserRole == 4) {
            setupUpdateProgressButton();
        }
    }

    private void loadAssigneeInfo(Task task) {
        executor.execute(() -> {
            String assigneeText = "Chưa được giao";
            
            if (task.assigned_to != null) {
                // Giao cho cá nhân
                User user = userRepository.getUserById(task.assigned_to);
                if (user != null) {
                    assigneeText = "Giao cho: " + user.name + " (" + user.email + ")";
                } else {
                    assigneeText = "Giao cho cá nhân (ID: " + task.assigned_to + ")";
                }
            } else if (task.team_id != null) {
                // Giao cho ban/team
                Team team = teamRepository.getTeamById(task.team_id);
                if (team != null) {
                    assigneeText = "Giao cho ban: " + team.name;
                } else {
                    assigneeText = "Giao cho ban (ID: " + task.team_id + ")";
                }
            }
            
            final String finalAssigneeText = assigneeText;
            runOnUiThread(() -> tvAssignedTo.setText(finalAssigneeText));
        });
    }

    private String getStatusText(String status) {
        if (status == null) return "Chưa bắt đầu";
        
        switch (status.toLowerCase()) {
            case "completed":
            case "hoàn thành":
                return "Hoàn thành";
            case "in_progress":
            case "đang thực hiện":
                return "Đang thực hiện";
            case "overdue":
            case "quá hạn":
                return "Quá hạn";
            default:
                return "Chưa bắt đầu";
        }
    }

    private void setStatusColor(String status) {
        int statusColor;
        
        switch (status != null ? status.toLowerCase() : "") {
            case "completed":
            case "hoàn thành":
                statusColor = R.color.green_500;
                break;
            case "in_progress":
            case "đang thực hiện":
                statusColor = R.color.blue_500;
                break;
            case "overdue":
            case "quá hạn":
                statusColor = R.color.red_500;
                break;
            default:
                statusColor = R.color.gray_500;
                break;
        }
        
        statusIndicator.setBackgroundColor(ContextCompat.getColor(this, statusColor));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // Refresh task details after update progress
            loadTaskDetails();
        } else if (requestCode == 1002 && resultCode == RESULT_OK) {
            // Refresh task details after edit
            loadTaskDetails();
        }
    }
} 