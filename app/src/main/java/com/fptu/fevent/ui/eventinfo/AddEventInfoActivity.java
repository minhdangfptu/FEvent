package com.fptu.fevent.ui.eventinfo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.fevent.R;
import com.fptu.fevent.database.AppDatabase;
import com.fptu.fevent.model.EventInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEventInfoActivity extends AppCompatActivity {
    private EditText etEventName, etEventLocation, etEventDescription;
    private Button btnStartTime, btnEndTime, btnCreateEvent, btnCancel;
    private AppDatabase database;
    private ExecutorService executorService;
    private Date startTime, endTime;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_info);

        getCurrentUserId();
        initViews();
        initDatabase();
        setupClickListeners();
    }

    private void getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        etEventName = findViewById(R.id.et_event_name);
        etEventLocation = findViewById(R.id.et_event_location);
        etEventDescription = findViewById(R.id.et_event_description);
        btnStartTime = findViewById(R.id.btn_start_time);
        btnEndTime = findViewById(R.id.btn_end_time);
        btnCreateEvent = findViewById(R.id.btn_create_event);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
    }

    private void setupClickListeners() {
        btnStartTime.setOnClickListener(v -> showDateTimePicker(true));
        btnEndTime.setOnClickListener(v -> showDateTimePicker(false));
        btnCreateEvent.setOnClickListener(v -> createEvent());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDateTimePicker(boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);
                                
                                Date selectedDateTime = calendar.getTime();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                
                                if (isStartTime) {
                                    startTime = selectedDateTime;
                                    btnStartTime.setText("Thời gian bắt đầu: " + sdf.format(selectedDateTime));
                                } else {
                                    endTime = selectedDateTime;
                                    btnEndTime.setText("Thời gian kết thúc: " + sdf.format(selectedDateTime));
                                }
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        
        datePickerDialog.show();
    }

    private void createEvent() {
        String name = etEventName.getText().toString().trim();
        String location = etEventLocation.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();

        if (name.isEmpty()) {
            etEventName.setError("Tên sự kiện không được để trống");
            return;
        }

        if (location.isEmpty()) {
            etEventLocation.setError("Địa điểm không được để trống");
            return;
        }

        if (description.isEmpty()) {
            etEventDescription.setError("Mô tả không được để trống");
            return;
        }

        if (startTime == null) {
            Toast.makeText(this, "Vui lòng chọn thời gian bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endTime == null) {
            Toast.makeText(this, "Vui lòng chọn thời gian kết thúc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endTime.before(startTime)) {
            Toast.makeText(this, "Thời gian kết thúc phải sau thời gian bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }

        EventInfo eventInfo = new EventInfo(0, name, startTime, endTime, location, description, currentUserId);

        executorService.execute(() -> {
            try {
                database.eventInfoDao().insert(eventInfo);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Tạo sự kiện thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tạo sự kiện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}

