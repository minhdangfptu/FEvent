package com.fptu.fevent.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.fptu.fevent.R;
import com.fptu.fevent.model.Schedule;
import com.fptu.fevent.repository.ScheduleRepository;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ScheduleDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvTime, tvLocation, tvDescription;
    private Button btnEdit, btnBack;
    private Schedule schedule;
    private ScheduleRepository repository;

    public static void start(Context context, int scheduleId) {
        Intent intent = new Intent(context, ScheduleDetailActivity.class);
        intent.putExtra("schedule_id", scheduleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        initViews();
        loadScheduleData();
        setupEventListeners();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvTime = findViewById(R.id.tv_time);
        tvLocation = findViewById(R.id.tv_location);
        tvDescription = findViewById(R.id.tv_description);
        btnEdit = findViewById(R.id.btn_edit);
        btnBack = findViewById(R.id.btn_back);
    }

    private void loadScheduleData() {
        int scheduleId = getIntent().getIntExtra("schedule_id", -1);

        if (scheduleId == -1) {
            Toast.makeText(this, "Không tìm thấy lịch trình", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository = new ScheduleRepository(getApplication());

        new Thread(() -> {
            schedule = repository.getScheduleByIdSync(scheduleId);
            runOnUiThread(() -> {
                if (schedule != null) {
                    displayScheduleData();
                } else {
                    Toast.makeText(this, "Lịch trình không tồn tại", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void displayScheduleData() {
        tvTitle.setText(schedule.title != null ? schedule.title : "Không có tiêu đề");
        String timeText = formatTimeRange(schedule.start_time, schedule.end_time);
        tvTime.setText(timeText);
        tvLocation.setText(schedule.location != null ? schedule.location : "Không có địa điểm");
        tvDescription.setText(schedule.description != null ? schedule.description : "Không có mô tả");
    }

    private String formatTimeRange(java.util.Date startTime, java.util.Date endTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        if (startTime == null && endTime == null) {
            return "Không có thời gian";
        }

        if (startTime == null) {
            return "Kết thúc: " + dateFormat.format(endTime) + " " + timeFormat.format(endTime);
        }

        if (endTime == null) {
            return "Bắt đầu: " + dateFormat.format(startTime) + " " + timeFormat.format(startTime);
        }

        if (dateFormat.format(startTime).equals(dateFormat.format(endTime))) {
            return dateFormat.format(startTime) + " (" + timeFormat.format(startTime) + " - " + timeFormat.format(endTime) + ")";
        } else {
            return dateFormat.format(startTime) + " " + timeFormat.format(startTime) + " - " +
                    dateFormat.format(endTime) + " " + timeFormat.format(endTime);
        }
    }

    private void setupEventListeners() {
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ScheduleDetailActivity.this, EditScheduleActivity.class);
            intent.putExtra("schedule_id", schedule.id);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (schedule != null) {
            loadScheduleData();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}