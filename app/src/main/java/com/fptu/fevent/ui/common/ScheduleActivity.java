package com.fptu.fevent.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.fevent.R;
import com.fptu.fevent.adapter.ScheduleAdapter;
import com.fptu.fevent.model.Schedule;
import com.fptu.fevent.repository.ScheduleRepository;
import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity implements ScheduleAdapter.OnScheduleClickListener, ScheduleAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private List<Schedule> scheduleList;
    private ScheduleRepository repository;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        repository = new ScheduleRepository(getApplication());
        scheduleList = new ArrayList<>();

        initViews();
        setupRecyclerView();
        checkUserPermissions();
        loadSchedules();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_schedules);
        btnCreate = findViewById(R.id.btn_create_schedule);
    }

    private void setupRecyclerView() {
        adapter = new ScheduleAdapter(scheduleList, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void checkUserPermissions() {
        int roleId = getCurrentUserRoleId();

        if (roleId == 2) {
            btnCreate.setVisibility(View.VISIBLE);
            btnCreate.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateScheduleActivity.class);
                startActivity(intent);
            });
        } else {
            btnCreate.setVisibility(View.GONE);
        }
    }

    private void loadSchedules() {
        new Thread(() -> {
            List<Schedule> schedules = repository.getAllSchedules();
            runOnUiThread(() -> {
                scheduleList.clear();
                scheduleList.addAll(schedules);
                adapter.updateSchedules(scheduleList);

                TextView emptyMessage = findViewById(R.id.tv_empty_message);
                if (emptyMessage != null) {
                    if (scheduleList.isEmpty()) {
                        emptyMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadSchedules();
    }

    @Override
    public void onEditClick(Schedule schedule) {
        int roleId = getCurrentUserRoleId();
        if (roleId != 2) {
            Toast.makeText(this, "Bạn không có quyền chỉnh sửa lịch họp", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, EditScheduleActivity.class);
        intent.putExtra("schedule_id", schedule.id);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Schedule schedule) {
        int roleId = getCurrentUserRoleId();
        if (roleId != 2) {
            Toast.makeText(this, "Bạn không có quyền xóa lịch họp", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa lịch trình \"" + schedule.title + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteSchedule(schedule);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    @Override
    public void onItemClick(Schedule schedule) {
        ScheduleDetailActivity.start(this, schedule.id);
    }

    private void deleteSchedule(Schedule schedule) {
        new Thread(() -> {
            repository.delete(schedule);
            runOnUiThread(() -> {
                Toast.makeText(this, "Đã xóa lịch trình", Toast.LENGTH_SHORT).show();
                loadSchedules(); // Refresh the list
            });
        }).start();
    }

    private int getCurrentUserRoleId() {
        return 2; // Placeholder - replace with actual user role logic
    }
}