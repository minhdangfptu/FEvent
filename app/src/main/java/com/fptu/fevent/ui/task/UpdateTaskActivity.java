package com.fptu.fevent.ui.task;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateTaskActivity extends AppCompatActivity {

    private TextView tvTitle, tvDescription, tvDueDate, tvAssignedTo, tvCurrentStatus;
    private AutoCompleteTextView actvNewStatus;
    private TextInputLayout tilNewStatus;
    private View currentStatusIndicator, newStatusIndicator;
    private MaterialButton btnBack, btnSave;
    private MaterialCardView cardCurrentStatus, cardNewStatus;
    private TextView tvAccessReason;
    
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private TeamRepository teamRepository;
    private ExecutorService executor;
    
    private Task currentTask;
    private int currentUserId;
    private int taskId;
    private String selectedStatus;
    
    private final String[] statusOptions = {
        "Chưa bắt đầu",
        "Đang thực hiện", 
        "Hoàn thành"
    };
    
    private final String[] statusValues = {
        "Chưa bắt đầu",
        "Đang thực hiện",
        "Hoàn thành"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_task);
        
        // Khởi tạo repositories
        taskRepository = new TaskRepository(getApplication());
        userRepository = new UserRepository(getApplication());
        teamRepository = new TeamRepository(getApplication());
        executor = Executors.newSingleThreadExecutor();
        
        // Lấy user ID hiện tại
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);
        
        // Lấy task ID từ intent
        taskId = getIntent().getIntExtra("task_id", -1);
        if (taskId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin công việc", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupStatusDropdown();
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
        tvDueDate = findViewById(R.id.tvDueDate);
        tvAssignedTo = findViewById(R.id.tvAssignedTo);
        tvCurrentStatus = findViewById(R.id.tvCurrentStatus);
        actvNewStatus = findViewById(R.id.actvNewStatus);
        tilNewStatus = findViewById(R.id.tilNewStatus);
        currentStatusIndicator = findViewById(R.id.currentStatusIndicator);
        newStatusIndicator = findViewById(R.id.newStatusIndicator);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        cardCurrentStatus = findViewById(R.id.cardCurrentStatus);
        cardNewStatus = findViewById(R.id.cardNewStatus);
        tvAccessReason = findViewById(R.id.tvAccessReason);
    }

    private void setupStatusDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, statusOptions);
        actvNewStatus.setAdapter(adapter);
        
        actvNewStatus.setOnItemClickListener((parent, view, position, id) -> {
            selectedStatus = statusValues[position];
            updateNewStatusIndicator(selectedStatus);
        });
        
        // Clear hint when dropdown is clicked
        actvNewStatus.setOnClickListener(v -> {
            if (actvNewStatus.getText().toString().isEmpty()) {
                actvNewStatus.showDropDown();
            }
        });
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());
        
        btnSave.setOnClickListener(v -> {
            if (selectedStatus == null || selectedStatus.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn trạng thái mới", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Chuẩn hóa status để so sánh
            String currentStatus = currentTask.status != null ? currentTask.status : "not_started";
            if (selectedStatus.equals(currentStatus)) {
                Toast.makeText(this, "Trạng thái mới giống với trạng thái hiện tại", Toast.LENGTH_SHORT).show();
                return;
            }
            
            updateTaskStatus();
        });
    }

    private void loadTaskDetails() {
        executor.execute(() -> {
            Task task = taskRepository.getTaskById(taskId);
            if (task != null) {
                // Kiểm tra xem user có quyền cập nhật task này không
                boolean canUpdate = false;
                String accessReason = "";
                
                if (task.assigned_to != null && task.assigned_to == currentUserId) {
                    canUpdate = true;
                    accessReason = "Công việc được giao cho bạn";
                } else if (task.team_id != null) {
                    // Tạm thời cho phép cập nhật nếu task được giao cho team
                    // TODO: Kiểm tra xem user có thuộc team này không trong tương lai
                    canUpdate = true;
                    accessReason = "Công việc được giao cho team của bạn";
                } else {
                    accessReason = "Công việc này không được giao cho bạn";
                }
                
                final String finalAccessReason = accessReason;
                
                if (!canUpdate) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Bạn không có quyền cập nhật công việc này. " + finalAccessReason, Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }
                
                currentTask = task;
                runOnUiThread(() -> {
                    displayTaskDetails(task);
                    tvAccessReason.setText(finalAccessReason);
                });
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
        tvCurrentStatus.setText(getStatusText(task.status));
        
        // Hiển thị màu cho trạng thái hiện tại
        setStatusColor(currentStatusIndicator, task.status);
        
        // Hiển thị ngày hết hạn
        if (task.due_date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvDueDate.setText(sdf.format(task.due_date));
        } else {
            tvDueDate.setText("Không có hạn chót");
        }
        
        // Load thông tin người/ban được giao
        loadAssigneeInfo(task);
        
        // Clear dropdown và reset selectedStatus
        actvNewStatus.setText("");
        selectedStatus = null;
        updateNewStatusIndicator("not_started"); // Màu mặc định
    }

    private void loadAssigneeInfo(Task task) {
        executor.execute(() -> {
            String assigneeText = "Chưa được giao";
            
            if (task.assigned_to != null) {
                User user = userRepository.getUserById(task.assigned_to);
                if (user != null) {
                    assigneeText = user.name + " (" + user.email + ")";
                } else {
                    assigneeText = "Cá nhân (ID: " + task.assigned_to + ")";
                }
            } else if (task.team_id != null) {
                Team team = teamRepository.getTeamById(task.team_id);
                if (team != null) {
                    assigneeText = "Ban: " + team.name;
                } else {
                    assigneeText = "Ban (ID: " + task.team_id + ")";
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

    private void setStatusColor(View indicator, String status) {
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
        
        indicator.setBackgroundColor(ContextCompat.getColor(this, statusColor));
    }

    private void updateNewStatusIndicator(String status) {
        setStatusColor(newStatusIndicator, status);
    }

    private void updateTaskStatus() {
        btnSave.setEnabled(false);
        btnSave.setText("Đang cập nhật...");
        
        executor.execute(() -> {
            try {
                currentTask.status = selectedStatus;
                taskRepository.update(currentTask);
                
                runOnUiThread(() -> {
                    Toast.makeText(this, "Cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSave.setEnabled(true);
                    btnSave.setText("Lưu thay đổi");
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
} 