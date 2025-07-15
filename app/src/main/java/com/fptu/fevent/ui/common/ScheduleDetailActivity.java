package com.fptu.fevent.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.fptu.fevent.R;
import com.fptu.fevent.model.Schedule;
import com.fptu.fevent.model.Team;
import com.fptu.fevent.repository.ScheduleRepository;
import com.fptu.fevent.repository.TeamRepository;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScheduleDetailActivity extends AppCompatActivity {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private TextView tvTitle, tvTime, tvLocation, tvDescription, tvScheduleType;
    private MaterialButton btnBack;
    
    private ScheduleRepository scheduleRepository;
    private TeamRepository teamRepository;
    
    public static void start(Context context, int scheduleId) {
        Intent intent = new Intent(context, ScheduleDetailActivity.class);
        intent.putExtra("schedule_id", scheduleId);
        context.startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);
        
        // Initialize repositories
        scheduleRepository = new ScheduleRepository(getApplication());
        teamRepository = new TeamRepository(getApplication());
        
        // Initialize views
        initViews();
        setupButtons();
        
        int scheduleId = getIntent().getIntExtra("schedule_id", -1);
        
        if (scheduleId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy lịch họp", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        loadScheduleDetails(scheduleId);
        
        // Handle window insets for proper edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvTime = findViewById(R.id.tv_time);
        tvLocation = findViewById(R.id.tv_location);
        tvDescription = findViewById(R.id.tv_description);
        tvScheduleType = findViewById(R.id.tv_schedule_type);
        btnBack = findViewById(R.id.btnBack);
    }
    
    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());
    }
    
    private void loadScheduleDetails(int scheduleId) {
        executor.execute(() -> {
            try {
                Schedule schedule = null;
                
                // Find schedule by ID
                for (Schedule s : scheduleRepository.getAll()) {
                    if (s.id == scheduleId) {
                        schedule = s;
                        break;
                    }
                }
                
                final Schedule finalSchedule = schedule;
                
                runOnUiThread(() -> {
                    if (finalSchedule != null) {
                        displayScheduleDetails(finalSchedule);
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin lịch họp", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tải thông tin lịch họp", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
    
    private void displayScheduleDetails(Schedule schedule) {
        // Set title
        tvTitle.setText(schedule.title);
        
        // Format and set time
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("vi", "VN"));
        String timeText = sdf.format(schedule.start_time) + " - " + sdf.format(schedule.end_time);
        tvTime.setText(timeText);
        
        // Set location
        tvLocation.setText(schedule.location);
        
        // Set description
        tvDescription.setText(schedule.description != null && !schedule.description.isEmpty() 
            ? schedule.description : "Không có mô tả");
        
        // Set schedule type based on team_id
        loadAndDisplayScheduleType(schedule.team_id);
    }
    
    private void loadAndDisplayScheduleType(Integer teamId) {
        if (teamId == null) {
            tvScheduleType.setText("Cuộc họp tổng (Tất cả thành viên)");
            tvScheduleType.setTextColor(ContextCompat.getColor(this, R.color.blue_600));
        } else {
            executor.execute(() -> {
                try {
                    Team team = null;
                    for (Team t : teamRepository.getAll()) {
                        if (t.id == teamId) {
                            team = t;
                            break;
                        }
                    }
                    
                    final Team finalTeam = team;
                    runOnUiThread(() -> {
                        if (finalTeam != null) {
                            tvScheduleType.setText("Cuộc họp " + finalTeam.name);
                            tvScheduleType.setTextColor(ContextCompat.getColor(this, R.color.green_500));
                        } else {
                            tvScheduleType.setText("Cuộc họp nhóm");
                            tvScheduleType.setTextColor(ContextCompat.getColor(this, R.color.orange_500));
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        tvScheduleType.setText("Cuộc họp nhóm");
                        tvScheduleType.setTextColor(ContextCompat.getColor(this, R.color.orange_500));
                    });
                }
            });
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}