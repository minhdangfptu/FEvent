package com.fptu.fevent.ui.task;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditTaskActivity extends AppCompatActivity {

    private TextInputEditText etTaskTitle, etTaskDescription;
    private TextView tvSelectedDate, tvCurrentStatus;
    private View statusIndicator;
    private AutoCompleteTextView actvAssignTo;
    private RadioGroup rgAssignType;
    private RadioButton rbAssignToUser, rbAssignToTeam;
    private MaterialButton btnBack, btnSelectDate, btnSave;
    
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private TeamRepository teamRepository;
    private ExecutorService executor;
    
    private Task currentTask;
    private Date selectedDate;
    private List<User> userList;
    private List<Team> teamList;
    private int currentUserRole;
    private int currentUserId;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_task);
        
        // Khởi tạo repositories
        taskRepository = new TaskRepository(getApplication());
        userRepository = new UserRepository(getApplication());
        teamRepository = new TeamRepository(getApplication());
        executor = Executors.newSingleThreadExecutor();
        
        // Lấy thông tin user hiện tại
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
        
        // Kiểm tra quyền chỉnh sửa
        if (currentUserRole != 1 && currentUserRole != 2 && currentUserRole != 3) {
            Toast.makeText(this, "Bạn không có quyền chỉnh sửa công việc", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupDatePicker();
        setupAssignmentType();
        setupButtons();
        loadUsersAndTeams();
        loadTaskDetails();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvCurrentStatus = findViewById(R.id.tvCurrentStatus);
        statusIndicator = findViewById(R.id.statusIndicator);
        actvAssignTo = findViewById(R.id.actvAssignTo);
        rgAssignType = findViewById(R.id.rgAssignType);
        rbAssignToUser = findViewById(R.id.rbAssignToUser);
        rbAssignToTeam = findViewById(R.id.rbAssignToTeam);
        btnBack = findViewById(R.id.btnBack);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupDatePicker() {
        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (selectedDate != null) {
                calendar.setTime(selectedDate);
            }
            
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth, 23, 59, 59);
                    selectedDate = selectedCalendar.getTime();
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = sdf.format(selectedDate);
                    tvSelectedDate.setText("Ngày hết hạn: " + formattedDate);
                    tvSelectedDate.setVisibility(View.VISIBLE);
                    btnSelectDate.setText("Thay đổi ngày");
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            
            // Không cho phép chọn ngày trong quá khứ
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
    }

    private void setupAssignmentType() {
        rgAssignType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAssignToUser) {
                setupUserDropdown();
            } else if (checkedId == R.id.rbAssignToTeam) {
                setupTeamDropdown();
            }
        });
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveTask());
    }

    private void loadUsersAndTeams() {
        executor.execute(() -> {
            userList = userRepository.getAllUsers();
            teamList = teamRepository.getAllTeams();
            
            runOnUiThread(() -> {
                // Setup dropdown sẽ được gọi sau khi load xong task details
            });
        });
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
        etTaskTitle.setText(task.title);
        etTaskDescription.setText(task.description != null ? task.description : "");
        
        // Hiển thị trạng thái hiện tại
        tvCurrentStatus.setText(getStatusText(task.status));
        setStatusColor(task.status);
        
        // Hiển thị ngày hết hạn
        selectedDate = task.due_date;
        if (selectedDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(selectedDate);
            tvSelectedDate.setText("Ngày hết hạn: " + formattedDate);
            tvSelectedDate.setVisibility(View.VISIBLE);
            btnSelectDate.setText("Thay đổi ngày");
        }
        
        // Setup assignment type và dropdown
        setupAssignmentFromTask(task);
    }

    private void setupAssignmentFromTask(Task task) {
        if (task.assigned_to != null) {
            // Giao cho cá nhân
            rbAssignToUser.setChecked(true);
            setupUserDropdown();
            
            // Tìm và set user được chọn
            if (userList != null) {
                for (User user : userList) {
                    if (user.getId() == task.assigned_to) {
                        String userDisplay = user.getFullName() + " (" + user.getEmail() + ")";
                        actvAssignTo.setText(userDisplay);
                        break;
                    }
                }
            }
        } else if (task.team_id != null) {
            // Giao cho team
            rbAssignToTeam.setChecked(true);
            setupTeamDropdown();
            
            // Tìm và set team được chọn
            if (teamList != null) {
                for (Team team : teamList) {
                    if (team.id == task.team_id) {
                        actvAssignTo.setText(team.name);
                        break;
                    }
                }
            }
        } else {
            // Mặc định chọn cá nhân
            rbAssignToUser.setChecked(true);
            setupUserDropdown();
        }
    }

    private void setupUserDropdown() {
        if (userList == null) return;
        
        List<String> userDisplayList = new ArrayList<>();
        for (User user : userList) {
            userDisplayList.add(user.getFullName() + " (" + user.getEmail() + ")");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, userDisplayList);
        actvAssignTo.setAdapter(adapter);
        actvAssignTo.setHint("Chọn người được giao việc");
    }

    private void setupTeamDropdown() {
        if (teamList == null) return;
        
        List<String> teamNameList = new ArrayList<>();
        for (Team team : teamList) {
            teamNameList.add(team.name);
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, teamNameList);
        actvAssignTo.setAdapter(adapter);
        actvAssignTo.setHint("Chọn ban được giao việc");
    }

    private void saveTask() {
        String title = etTaskTitle.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();
        String assignToText = actvAssignTo.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            etTaskTitle.setError("Vui lòng nhập tiêu đề công việc");
            etTaskTitle.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            etTaskDescription.setError("Vui lòng nhập mô tả công việc");
            etTaskDescription.requestFocus();
            return;
        }

        if (assignToText.isEmpty()) {
            actvAssignTo.setError("Vui lòng chọn người hoặc ban được giao việc");
            actvAssignTo.requestFocus();
            return;
        }

        if (selectedDate == null) {
            Toast.makeText(this, "Vui lòng chọn ngày hết hạn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin task
        currentTask.title = title;
        currentTask.description = description;
        currentTask.due_date = selectedDate;

        // Xác định người/ban được giao
        if (rbAssignToUser.isChecked()) {
            // Giao cho user
            Integer userId = findUserIdByDisplayText(assignToText);
            if (userId != null) {
                currentTask.assigned_to = userId;
                currentTask.team_id = null;
            } else {
                Toast.makeText(this, "Không tìm thấy người dùng được chọn", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // Giao cho team
            Integer teamId = findTeamIdByName(assignToText);
            if (teamId != null) {
                currentTask.team_id = teamId;
                currentTask.assigned_to = null;
            } else {
                Toast.makeText(this, "Không tìm thấy ban được chọn", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Lưu task
        executor.execute(() -> {
            taskRepository.update(currentTask);
            runOnUiThread(() -> {
                Toast.makeText(this, "Cập nhật công việc thành công!", Toast.LENGTH_SHORT).show();
                
                // Trả kết quả về TaskDetailActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("task_updated", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            });
        });
    }

    private Integer findUserIdByDisplayText(String displayText) {
        if (userList == null) return null;
        
        for (User user : userList) {
            String userDisplay = user.getFullName() + " (" + user.getEmail() + ")";
            if (userDisplay.equals(displayText)) {
                return user.getId();
            }
        }
        return null;
    }

    private Integer findTeamIdByName(String teamName) {
        if (teamList == null) return null;
        
        for (Team team : teamList) {
            if (team.name.equals(teamName)) {
                return team.id;
            }
        }
        return null;
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
} 