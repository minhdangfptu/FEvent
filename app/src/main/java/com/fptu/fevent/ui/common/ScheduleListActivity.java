package com.fptu.fevent.ui.common;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.fevent.R;
import com.fptu.fevent.adapter.ScheduleAdapter;
import com.fptu.fevent.model.Schedule;
import com.fptu.fevent.repository.ScheduleRepository;
import java.util.List;

public class ScheduleListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private ScheduleRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);
        recyclerView = findViewById(R.id.recycler_schedules);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        repository = new ScheduleRepository(getApplication());
        loadSchedules();
    }

    private void loadSchedules() {
        List<Schedule> schedules = repository.getAll();
        adapter = new ScheduleAdapter(schedules, schedule -> {
            ScheduleDetailActivity.start(this, schedule.id);
        });
        recyclerView.setAdapter(adapter);
    }
}