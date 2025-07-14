package com.fptu.fevent.ui.common;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fptu.fevent.R;

public class UpdateScheduleAttendanceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int roleId = getCurrentUserRoleId();
        if (roleId != 4) {
            Toast.makeText(this, "Bạn không có quyền cập nhật tham gia họp", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_update_schedule_attendance);

        RadioGroup radioGroup = findViewById(R.id.radio_attendance);
        EditText etReason = findViewById(R.id.et_reason);
        Button btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(v -> {
            int checked = radioGroup.getCheckedRadioButtonId();
            String reason = etReason.getText().toString();
            Toast.makeText(this, "Attendance updated", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private int getCurrentUserRoleId() {
        return 4;
    }
}