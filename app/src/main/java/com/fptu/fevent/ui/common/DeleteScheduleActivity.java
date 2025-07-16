package com.fptu.fevent.ui.common;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.fptu.fevent.model.Schedule;
import com.fptu.fevent.repository.ScheduleRepository;

public class DeleteScheduleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int roleId = getCurrentUserRoleId();
        if (roleId != 2) {
            Toast.makeText(this, "Bạn không có quyền tạo lịch họp", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int scheduleId = getIntent().getIntExtra("schedule_id", -1);
        if (scheduleId == -1) {
            Toast.makeText(this, "Schedule ID không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ScheduleRepository repository = new ScheduleRepository(getApplication());

        // Thực hiện truy vấn và xoá trong background thread
        new Thread(() -> {
            Schedule scheduleToDelete = null;
            for (Schedule s : repository.getAll()) {
                if (s.id == scheduleId) {
                    scheduleToDelete = s;
                    break;
                }
            }

            if (scheduleToDelete != null) {
                repository.delete(scheduleToDelete);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lịch họp đã được xoá", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Không tìm thấy lịch họp", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private int getCurrentUserRoleId() {
        // TODO: Lấy role thực tế từ user login/session
        return 2; // Giả định đang là admin
    }
}
