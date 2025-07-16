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

public class CreateTeamActivity extends AppCompatActivity {
    private EditText etTeamName, etTeamDescription;
    private Button btnCreateTeam, btnCancel;
    private AppDatabase database;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        initViews();
        initDatabase();
        setupClickListeners();
    }

    private void initViews() {
        etTeamName = findViewById(R.id.et_team_name);
        etTeamDescription = findViewById(R.id.et_team_description);
        btnCreateTeam = findViewById(R.id.btn_create_team);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
    }

    private void setupClickListeners() {
        btnCreateTeam.setOnClickListener(v -> createTeam());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void createTeam() {
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

        Team team = new Team(0, name, description);

        executorService.execute(() -> {
            try {
                database.teamDao().insert(team);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Tạo ban thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tạo ban: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

