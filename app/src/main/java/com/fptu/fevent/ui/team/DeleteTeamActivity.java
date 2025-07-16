package com.fptu.fevent.ui.team;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.fevent.R;
import com.fptu.fevent.database.AppDatabase;
import com.fptu.fevent.model.Team;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteTeamActivity extends AppCompatActivity {
    private TextView tvTeamName, tvTeamDescription;
    private Button btnDeleteTeam, btnCancel;
    private AppDatabase database;
    private ExecutorService executorService;
    private int teamId;
    private Team currentTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_team);

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
        tvTeamName = findViewById(R.id.tv_team_name);
        tvTeamDescription = findViewById(R.id.tv_team_description);
        btnDeleteTeam = findViewById(R.id.btn_delete_team);
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
                    tvTeamName.setText(currentTeam.name);
                    tvTeamDescription.setText(currentTeam.description);
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
        btnDeleteTeam.setOnClickListener(v -> showDeleteConfirmDialog());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa ban \"" + currentTeam.name + "\"?\nHành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteTeam())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteTeam() {
        executorService.execute(() -> {
            try {
                database.teamDao().delete(currentTeam);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Xóa ban thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi xóa ban: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

