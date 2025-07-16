package com.fptu.fevent.ui.common;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fptu.fevent.R;
import com.fptu.fevent.model.Schedule;
import com.fptu.fevent.repository.ScheduleRepository;
import com.fptu.fevent.service.NotificationService;
import com.fptu.fevent.service.LocationApiClient;
import com.fptu.fevent.service.LocationResult;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateScheduleActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private AutoCompleteTextView etLocation;
    private Button btnStartTime, btnEndTime, btnChooseLocation;
    private Date startTime, endTime;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private ScheduleRepository repository;
    private NotificationService notificationService;

    // REQUEST CODE cho chọn vị trí từ bản đồ
    private static final int REQUEST_MAP_LOCATION = 100;

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
        notificationService = new NotificationService(getApplication());

        // Ánh xạ view
        etTitle = findViewById(R.id.et_title);
        MaterialButton btnChooseLocation = findViewById(R.id.btn_choose_location);
        btnChooseLocation.setVisibility(View.VISIBLE);
        etLocation = findViewById(R.id.et_location);
        etDescription = findViewById(R.id.et_description);
        btnStartTime = findViewById(R.id.btn_start_time);
        btnEndTime = findViewById(R.id.btn_end_time);
        Button btnSave = findViewById(R.id.btn_save);
        btnChooseLocation = findViewById(R.id.btn_choose_location); // Nút chọn vị trí từ bản đồ

        // Gợi ý địa điểm (dùng API Nominatim)
        etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    LocationApiClient.getLocationApiService()
                            .searchLocations(s.toString(), "json", 5)
                            .enqueue(new Callback<List<LocationResult>>() {
                                @Override
                                public void onResponse(Call<List<LocationResult>> call, Response<List<LocationResult>> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        List<LocationResult> locations = response.body();
                                        ArrayAdapter<LocationResult> adapter = new ArrayAdapter<>(
                                                CreateScheduleActivity.this,
                                                android.R.layout.simple_dropdown_item_1line,
                                                locations
                                        );
                                        etLocation.setAdapter(adapter);
                                        etLocation.showDropDown();
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<LocationResult>> call, Throwable t) {
                                    Log.e("API_ERROR", "Location fetch failed: " + t.getMessage());
                                }
                            });
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        // Chọn thời gian
        btnStartTime.setOnClickListener(v -> pickDateTime(true));
        btnEndTime.setOnClickListener(v -> pickDateTime(false));

        // Chọn vị trí từ bản đồ
        btnChooseLocation.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectLocationActivity.class);
            startActivityForResult(intent, REQUEST_MAP_LOCATION);
        });

        // Lưu lịch
        btnSave.setOnClickListener(v -> saveSchedule());
    }

    // Nhận vị trí trả về từ bản đồ
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MAP_LOCATION && resultCode == RESULT_OK && data != null) {
            double lat = data.getDoubleExtra("latitude", 0.0);
            double lng = data.getDoubleExtra("longitude", 0.0);
            etLocation.setText(lat + ", " + lng); // Hiển thị vị trí hoặc gọi reverse geocoding tại đây
        }
    }

    private int getCurrentUserRoleId() {
        return 2; // Giả lập quyền
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

        // Lưu đồng bộ
        new Thread(() -> {
            repository.insert(schedule);
            runOnUiThread(() -> {
                Toast.makeText(this, "Schedule created", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();

        // Lưu bất đồng bộ + gửi thông báo
        repository.insertAsync(schedule, insertedId -> {
            if (insertedId > 0) {
                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                int currentUserId = prefs.getInt("user_id", -1);
                schedule.id = insertedId.intValue();

                notificationService.notifyScheduleCreated(schedule, currentUserId);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Schedule created and notifications sent", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to create schedule", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
