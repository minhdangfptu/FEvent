package com.fptu.fevent.ui.task;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText etTaskTitle, etTaskDescription;
    private TextView tvSelectedDate;
    private AutoCompleteTextView actvAssignTo;
    private RadioGroup rgAssignType;
    private RadioButton rbAssignToUser, rbAssignToTeam;
    private Button btnSelectDate, btnCreateTask;
    private ImageView ivBack;

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private TeamRepository teamRepository;
    private ExecutorService executor;

    private Date selectedDate;
    private List<User> userList;
    private List<Team> teamList;
    private int currentUserRole;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);

        // Khởi tạo repositories
        taskRepository = new TaskRepository(getApplication());
        userRepository = new UserRepository(getApplication());
        teamRepository = new TeamRepository(getApplication());
        executor = Executors.newSingleThreadExecutor();

        // Lấy thông tin user hiện tại
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserRole = prefs.getInt("role_id", 4);
        currentUserId = prefs.getInt("user_id", 0);

        initViews();
        setupDatePicker();
        setupAssignmentType();
        loadUsersAndTeams();
        setupButtons();

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
        actvAssignTo = findViewById(R.id.actvAssignTo);
        rgAssignType = findViewById(R.id.rgAssignType);
        rbAssignToUser = findViewById(R.id.rbAssignToUser);
        rbAssignToTeam = findViewById(R.id.rbAssignToTeam);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        ivBack = findViewById(R.id.ivBack);
    }

    private void setupDatePicker() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth);
                    selectedDate = selectedCalendar.getTime();
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    tvSelectedDate.setText(sdf.format(selectedDate));
                    tvSelectedDate.setVisibility(View.VISIBLE);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Không cho chọn ngày trong quá khứ
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void setupAssignmentType() {
        rgAssignType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAssignToUser) {
                loadUserList();
            } else if (checkedId == R.id.rbAssignToTeam) {
                loadTeamList();
            }
        });
        
        // Mặc định chọn giao cho user
        rbAssignToUser.setChecked(true);
    }

    private void loadUsersAndTeams() {
        executor.execute(() -> {
            userList = userRepository.getAll();
            teamList = teamRepository.getAll();
            
            runOnUiThread(() -> {
                // Load user list mặc định
                loadUserList();
            });
        });
    }

    private void loadUserList() {
        if (userList == null) return;
        
        List<String> userNames = new ArrayList<>();
        for (User user : userList) {
            userNames.add(user.getFullName() + " (" + user.getEmail() + ")");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, userNames);
        actvAssignTo.setAdapter(adapter);
        actvAssignTo.setHint("Chọn người được giao việc");
    }

    private void loadTeamList() {
        if (teamList == null) return;
        
        List<String> teamNames = new ArrayList<>();
        for (Team team : teamList) {
            teamNames.add(team.name);
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, teamNames);
        actvAssignTo.setAdapter(adapter);
        actvAssignTo.setHint("Chọn ban được giao việc");
    }

    private void setupButtons() {
        ivBack.setOnClickListener(v -> finish());
        
        btnCreateTask.setOnClickListener(v -> createTask());
    }

    private void createTask() {
        String title = etTaskTitle.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();
        String assignToText = actvAssignTo.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            etTaskTitle.setError("Vui lòng nhập tiêu đề công việc");
            return;
        }

        if (description.isEmpty()) {
            etTaskDescription.setError("Vui lòng nhập mô tả công việc");
            return;
        }

        if (assignToText.isEmpty()) {
            actvAssignTo.setError("Vui lòng chọn người hoặc ban được giao việc");
            return;
        }

        // Tạo task object
        Task newTask = new Task();
        newTask.title = title;
        newTask.description = description;
        newTask.status = "Chưa bắt đầu";
        newTask.due_date = selectedDate;

        // Xác định người/ban được giao
        if (rbAssignToUser.isChecked()) {
            // Giao cho user
            Integer userId = findUserIdByDisplayText(assignToText);
            if (userId != null) {
                newTask.assigned_to = userId;
                newTask.team_id = null;
            } else {
                Toast.makeText(this, "Không tìm thấy người dùng được chọn", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // Giao cho team
            Integer teamId = findTeamIdByName(assignToText);
            if (teamId != null) {
                newTask.team_id = teamId;
                newTask.assigned_to = null;
            } else {
                Toast.makeText(this, "Không tìm thấy ban được chọn", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Lưu task
        executor.execute(() -> {
            taskRepository.insert(newTask);
            runOnUiThread(() -> {
                Toast.makeText(this, "Tạo công việc thành công!", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
} 