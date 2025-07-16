package com.fptu.fevent.ui.eventinfo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

public class EditEventInfoActivity extends AppCompatActivity {
    private EditText etEventName, etEventLocation, etEventDescription;
    private Button btnStartTime, btnEndTime, btnUpdateEvent, btnCancel;
    private AppDatabase database;
    private ExecutorService executorService;
    private Date startTime, endTime;
    private int eventId;
    private EventInfo currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event_info);

        eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin sự kiện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initDatabase();
        setupClickListeners();
        loadEventData();
    }

    private void initViews() {
        etEventName = findViewById(R.id.et_event_name);
        etEventLocation = findViewById(R.id.et_event_location);
        etEventDescription = findViewById(R.id.et_event_description);
        btnStartTime = findViewById(R.id.btn_start_time);
        btnEndTime = findViewById(R.id.btn_end_time);
        btnUpdateEvent = findViewById(R.id.btn_update_event);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
    }

    private void setupClickListeners() {
        btnStartTime.setOnClickListener(v -> showDateTimePicker(true));
        btnEndTime.setOnClickListener(v -> showDateTimePicker(false));
        btnUpdateEvent.setOnClickListener(v -> updateEvent());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadEventData() {
        executorService.execute(() -> {
            try {
                currentEvent = database.eventInfoDao().getById(eventId);
                if (currentEvent != null) {
                    runOnUiThread(() -> {
                        displayEventInfo();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Không tìm thấy thông tin sự kiện", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tải thông tin sự kiện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void displayEventInfo() {
        etEventName.setText(currentEvent.name);
        etEventLocation.setText(currentEvent.location);
        etEventDescription.setText(currentEvent.description);
        
        startTime = currentEvent.start_time;
        endTime = currentEvent.end_time;
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        
        if (startTime != null) {
            btnStartTime.setText("Thời gian bắt đầu: " + sdf.format(startTime));
        }
        
        if (endTime != null) {
            btnEndTime.setText("Thời gian kết thúc: " + sdf.format(endTime));
        }
    }

    private void showDateTimePicker(boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        
        // Set initial date/time if available
        if (isStartTime && startTime != null) {
            calendar.setTime(startTime);
        } else if (!isStartTime && endTime != null) {
            calendar.setTime(endTime);
        }
        
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

    private void updateEvent() {
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

        currentEvent.name = name;
        currentEvent.location = location;
        currentEvent.description = description;
        currentEvent.start_time = startTime;
        currentEvent.end_time = endTime;

        executorService.execute(() -> {
            try {
                database.eventInfoDao().update(currentEvent);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Cập nhật sự kiện thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi cập nhật sự kiện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

