package com.fptu.fevent.ui.event;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.fptu.fevent.R;
import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.viewmodel.EventInfoViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditEventActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_ID = "com.fptu.fevent.EXTRA_EVENT_ID";
    public static final String EXTRA_EVENT_NAME = "com.fptu.fevent.EXTRA_EVENT_NAME";
    public static final String EXTRA_EVENT_DESC = "com.fptu.fevent.EXTRA_EVENT_DESC";

    private TextInputEditText editTextName, editTextDescription, editTextStartDate, editTextEndDate;
    private EventInfoViewModel eventInfoViewModel;
    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();
    private int eventId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_FEvent_NoActionBar);
        setContentView(R.layout.activity_add_edit_event);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_event);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Ánh xạ View
        editTextName = findViewById(R.id.edit_text_event_name);
        editTextDescription = findViewById(R.id.edit_text_event_description);
        editTextStartDate = findViewById(R.id.edit_text_start_date);
        editTextEndDate = findViewById(R.id.edit_text_end_date);

        eventInfoViewModel = new ViewModelProvider(this).get(EventInfoViewModel.class);

        // Kiểm tra chế độ Sửa
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_EVENT_ID)) {
            setTitle("Chỉnh sửa sự kiện");
            eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1);
            editTextName.setText(intent.getStringExtra(EXTRA_EVENT_NAME));
            editTextDescription.setText(intent.getStringExtra(EXTRA_EVENT_DESC));
            // (Bạn có thể truyền và set cả ngày tháng ở đây nếu cần)
        } else {
            setTitle("Tạo sự kiện mới");
        }

        // Thiết lập DatePicker
        setupDatePicker();
    }

    private void setupDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        DatePickerDialog.OnDateSetListener startDateSetListener = (view, year, month, dayOfMonth) -> {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            editTextStartDate.setText(sdf.format(startCalendar.getTime()));
        };

        DatePickerDialog.OnDateSetListener endDateSetListener = (view, year, month, dayOfMonth) -> {
            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, month);
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            editTextEndDate.setText(sdf.format(endCalendar.getTime()));
        };

        editTextStartDate.setOnClickListener(v -> new DatePickerDialog(AddEditEventActivity.this,
                startDateSetListener,
                startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH),
                startCalendar.get(Calendar.DAY_OF_MONTH)).show());

        editTextEndDate.setOnClickListener(v -> new DatePickerDialog(AddEditEventActivity.this,
                endDateSetListener,
                endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH),
                endCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void saveEvent() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String startDateStr = editTextStartDate.getText().toString().trim();

        if (name.isEmpty() || startDateStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và ngày bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }

        EventInfo eventInfo = new EventInfo();
        eventInfo.name = name;
        eventInfo.description = description;
        eventInfo.start_time = startCalendar.getTime();
        eventInfo.end_time = endCalendar.getTime();

        if (eventId != -1) {
            eventInfo.id = eventId;
            eventInfoViewModel.update(eventInfo);
            Toast.makeText(this, "Đã cập nhật sự kiện", Toast.LENGTH_SHORT).show();
        } else {
            eventInfoViewModel.insert(eventInfo);
            Toast.makeText(this, "Đã tạo sự kiện", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveEvent();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}