package com.fptu.fevent.ui.common;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fptu.fevent.R;
import com.fptu.fevent.model.Schedule;
import com.fptu.fevent.repository.ScheduleRepository;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateScheduleActivity extends AppCompatActivity {
    private EditText etTitle, etLocation, etDescription;
    private Button btnStartTime;
    private Button btnEndTime;
    private Date startTime, endTime;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private ScheduleRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int roleId = getCurrentUserRoleId();
        if (roleId != 2) {
            Toast.makeText(this, "Bạn không có quyền tạo lịch họp", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setContentView(R.layout.activity_create_schedule);
        repository = new ScheduleRepository(getApplication());

        etTitle = findViewById(R.id.et_title);
        etLocation = findViewById(R.id.et_location);
        etDescription = findViewById(R.id.et_description);
        btnStartTime = findViewById(R.id.btn_start_time);
        btnEndTime = findViewById(R.id.btn_end_time);
        Button btnSave = findViewById(R.id.btn_save);

        btnStartTime.setOnClickListener(v -> pickDateTime(true));
        btnEndTime.setOnClickListener(v -> pickDateTime(false));
        btnSave.setOnClickListener(v -> saveSchedule());
    }

    private int getCurrentUserRoleId() {
        return 2;
    }

    private void pickDateTime(boolean isStart) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            new TimePickerDialog(this, (v, hour, min) -> {
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, min);
                if (isStart) {
                    startTime = cal.getTime();
                    btnStartTime.setText(sdf.format(startTime));
                } else {
                    endTime = cal.getTime();
                    btnEndTime.setText(sdf.format(endTime));
                }
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveSchedule() {
        String title = etTitle.getText().toString();
        String location = etLocation.getText().toString();
        String description = etDescription.getText().toString();
        if (title.isEmpty() || startTime == null || endTime == null || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Schedule schedule = new Schedule();
        schedule.title = title;
        schedule.start_time = startTime;
        schedule.end_time = endTime;
        schedule.location = location;
        schedule.description = description;
        repository.insert(schedule);
        Toast.makeText(this, "Schedule created", Toast.LENGTH_SHORT).show();
        finish();
    }
}