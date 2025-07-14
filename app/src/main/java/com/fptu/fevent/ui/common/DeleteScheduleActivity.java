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
        ScheduleRepository repository = new ScheduleRepository(getApplication());
        Schedule schedule = null;
        for (Schedule s : repository.getAll()) {
            if (s.id == scheduleId) {
                schedule = s;
                break;
            }
        }
        if (schedule != null) {
            repository.delete(schedule);
            Toast.makeText(this, "Schedule deleted", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private int getCurrentUserRoleId() {
        return 2;
    }
}