package com.fptu.fevent.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
    private TextView tvTitle, tvTime, tvLocation, tvDescription, tvScheduleType;
    private MaterialButton btnBack;
    private ScheduleRepository scheduleRepository;
    private TeamRepository teamRepository;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void start(Context context, int scheduleId) {
        Intent intent = new Intent(context, ScheduleDetailActivity.class);
        intent.putExtra("schedule_id", scheduleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        scheduleRepository = new ScheduleRepository(getApplication());
        teamRepository = new TeamRepository(getApplication());

        initViews();
        setupButtons();

        int scheduleId = getIntent().getIntExtra("schedule_id", -1);
        if (scheduleId == -1) {
            Toast.makeText(this, "Không tìm thấy lịch trình", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadScheduleDetails(scheduleId);
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
            Schedule schedule = scheduleRepository.getScheduleByIdSync(scheduleId);
            runOnUiThread(() -> {
                if (schedule != null) {
                    displayScheduleDetails(schedule);
                } else {
                    Toast.makeText(this, "Lịch trình không tồn tại", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayScheduleDetails(Schedule schedule) {
        tvTitle.setText(schedule.title != null ? schedule.title : "Không có tiêu đề");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        if (schedule.start_time != null && schedule.end_time != null) {
            if (dateFormat.format(schedule.start_time).equals(dateFormat.format(schedule.end_time))) {
                tvTime.setText(dateFormat.format(schedule.start_time) + " (" +
                        timeFormat.format(schedule.start_time) + " - " +
                        timeFormat.format(schedule.end_time) + ")");
            } else {
                tvTime.setText(dateFormat.format(schedule.start_time) + " " +
                        timeFormat.format(schedule.start_time) + " - " +
                        dateFormat.format(schedule.end_time) + " " +
                        timeFormat.format(schedule.end_time));
            }
        } else if (schedule.start_time != null) {
            tvTime.setText("Bắt đầu: " + dateFormat.format(schedule.start_time) + " " +
                    timeFormat.format(schedule.start_time));
        } else if (schedule.end_time != null) {
            tvTime.setText("Kết thúc: " + dateFormat.format(schedule.end_time) + " " +
                    timeFormat.format(schedule.end_time));
        } else {
            tvTime.setText("Không có thời gian");
        }

        tvLocation.setText(schedule.location != null ? schedule.location : "Không có địa điểm");
        tvDescription.setText(schedule.description != null && !schedule.description.isEmpty()
                ? schedule.description : "Không có mô tả");

        loadAndDisplayScheduleType(schedule.team_id);
    }

    private void loadAndDisplayScheduleType(Integer teamId) {
        if (teamId == null) {
            tvScheduleType.setText("Cuộc họp tổng (Tất cả thành viên)");
            tvScheduleType.setTextColor(ContextCompat.getColor(this, R.color.blue_600));
        } else {
            executor.execute(() -> {
                Team team = teamRepository.getTeamByIdSync(teamId);
                runOnUiThread(() -> {
                    if (team != null) {
                        tvScheduleType.setText("Cuộc họp " + team.name);
                        tvScheduleType.setTextColor(ContextCompat.getColor(this, R.color.green_500));
                    } else {
                        tvScheduleType.setText("Cuộc họp nhóm");
                        tvScheduleType.setTextColor(ContextCompat.getColor(this, R.color.orange_500));
                    }
                });
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
