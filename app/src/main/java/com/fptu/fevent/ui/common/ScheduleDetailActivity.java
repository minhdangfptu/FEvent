package com.fptu.fevent.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.fptu.fevent.R;
import com.fptu.fevent.model.Schedule;
import com.fptu.fevent.repository.ScheduleRepository;

public class ScheduleDetailActivity extends AppCompatActivity {
    public static void start(Context context, int scheduleId) {
        Intent intent = new Intent(context, ScheduleDetailActivity.class);
        intent.putExtra("schedule_id", scheduleId);
        context.startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);
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
            ((TextView) findViewById(R.id.tv_title)).setText(schedule.title);
            ((TextView) findViewById(R.id.tv_time)).setText(schedule.start_time + " - " + schedule.end_time);
            ((TextView) findViewById(R.id.tv_location)).setText(schedule.location);
            ((TextView) findViewById(R.id.tv_description)).setText(schedule.description);
        }
    }
}