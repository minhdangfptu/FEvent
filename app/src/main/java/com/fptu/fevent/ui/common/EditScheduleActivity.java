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

public class EditScheduleActivity extends AppCompatActivity {
    private EditText etTitle, etLocation, etDescription;
    private Date startTime, endTime;
    private ScheduleRepository repository;
    private Schedule schedule;
    private Button btnStartTime, btnEndTime;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int roleId = getCurrentUserRoleId();
        if (roleId != 2) {
            Toast.makeText(this, "Bạn không có quyền tạo lịch họp", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setContentView(R.layout.activity_edit_schedule);

        int scheduleId = getIntent().getIntExtra("schedule_id", -1);
        repository = new ScheduleRepository(getApplication());

        etTitle = findViewById(R.id.et_title);
        etLocation = findViewById(R.id.et_location);
        etDescription = findViewById(R.id.et_description);
        btnStartTime = findViewById(R.id.btn_start_time);
        btnEndTime = findViewById(R.id.btn_end_time);
        Button btnSave = findViewById(R.id.btn_save);

        new Thread(() -> {
            for (Schedule s : repository.getAll()) {
                if (s.id == scheduleId) {
                    schedule = s;
                    break;
                }
            }
            runOnUiThread(() -> {
                if (schedule != null) {
                    etTitle.setText(schedule.title);
                    etLocation.setText(schedule.location);
                    etDescription.setText(schedule.description);
                    startTime = schedule.start_time;
                    endTime = schedule.end_time;
                    btnStartTime.setText(sdf.format(startTime));
                    btnEndTime.setText(sdf.format(endTime));
                }
            });
        }).start();

        // Thiết lập event listeners
        btnStartTime.setOnClickListener(v -> pickDateTime(true, btnStartTime));
        btnEndTime.setOnClickListener(v -> pickDateTime(false, btnEndTime));
        btnSave.setOnClickListener(v -> {
            if (schedule != null) {
                schedule.title = etTitle.getText().toString();
                schedule.location = etLocation.getText().toString();
                schedule.description = etDescription.getText().toString();
                schedule.start_time = startTime;
                schedule.end_time = endTime;
                repository.update(schedule);
                Toast.makeText(this, "Schedule updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void pickDateTime(boolean isStart, Button button) {
        final Calendar calendar = Calendar.getInstance();
        Date initialDate = isStart ? startTime : endTime;
        if (initialDate != null) {
            calendar.setTime(initialDate);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, y, m, d) -> {
            calendar.set(Calendar.YEAR, y);
            calendar.set(Calendar.MONTH, m);
            calendar.set(Calendar.DAY_OF_MONTH, d);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(this, (tpView, h, min) -> {
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, min);
                Date selectedDate = calendar.getTime();
                if (isStart) {
                    startTime = selectedDate;
                } else {
                    endTime = selectedDate;
                }
                button.setText(sdf.format(selectedDate));
            }, hour, minute, true).show();

        }, year, month, day).show();
    }

    private int getCurrentUserRoleId() {
        return 2;
    }
}