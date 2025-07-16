package com.fptu.fevent.ui.team;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.fevent.R;
import com.fptu.fevent.database.AppDatabase;
import com.fptu.fevent.model.Team;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditTeamActivity extends AppCompatActivity {
    private EditText etTeamName, etTeamDescription;
    private Button btnUpdateTeam, btnCancel;
    private AppDatabase database;
    private ExecutorService executorService;
    private int teamId;
    private Team currentTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team);

        teamId = getIntent().getIntExtra("team_id", -1);
        if (teamId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin ban", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initDatabase();
        loadTeamData();
        setupClickListeners();
    }

    private void initViews() {
        etTeamName = findViewById(R.id.et_team_name);
        etTeamDescription = findViewById(R.id.et_team_description);
        btnUpdateTeam = findViewById(R.id.btn_update_team);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
    }

    private void loadTeamData() {
        executorService.execute(() -> {
            currentTeam = database.teamDao().getById(teamId);
            if (currentTeam != null) {
                runOnUiThread(() -> {
                    etTeamName.setText(currentTeam.name);
                    etTeamDescription.setText(currentTeam.description);
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Không tìm thấy thông tin ban", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void setupClickListeners() {
        btnUpdateTeam.setOnClickListener(v -> updateTeam());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void updateTeam() {
        String name = etTeamName.getText().toString().trim();
        String description = etTeamDescription.getText().toString().trim();

        if (name.isEmpty()) {
            etTeamName.setError("Tên ban không được để trống");
            return;
        }

        if (description.isEmpty()) {
            etTeamDescription.setError("Mô tả ban không được để trống");
            return;
        }

        currentTeam.name = name;
        currentTeam.description = description;

        executorService.execute(() -> {
            try {
                database.teamDao().update(currentTeam);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Cập nhật ban thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi cập nhật ban: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

