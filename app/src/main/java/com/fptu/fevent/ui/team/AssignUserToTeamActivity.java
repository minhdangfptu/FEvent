package com.fptu.fevent.ui.team;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.fevent.R;
import com.fptu.fevent.database.AppDatabase;
import com.fptu.fevent.model.Team;
import com.fptu.fevent.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssignUserToTeamActivity extends AppCompatActivity {
    private Spinner spinnerUsers, spinnerTeams;
    private Button btnAssign, btnCancel;
    private AppDatabase database;
    private ExecutorService executorService;
    private List<User> users;
    private List<Team> teams;
    private ArrayAdapter<String> userAdapter, teamAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_user_to_team);

        initViews();
        initDatabase();
        setupSpinners();
        setupClickListeners();
        loadData();
    }

    private void initViews() {
        spinnerUsers = findViewById(R.id.spinner_users);
        spinnerTeams = findViewById(R.id.spinner_teams);
        btnAssign = findViewById(R.id.btn_assign);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        users = new ArrayList<>();
        teams = new ArrayList<>();
    }

    private void setupSpinners() {
        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(userAdapter);

        teamAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeams.setAdapter(teamAdapter);
    }

    private void setupClickListeners() {
        btnAssign.setOnClickListener(v -> assignUserToTeam());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadData() {
        executorService.execute(() -> {
            try {
                // Load users
                List<User> userList = database.userDao().getAll();
                List<String> userNames = new ArrayList<>();
                for (User user : userList) {
                    userNames.add(user.fullname + " (" + user.email + ")");
                }

                // Load teams
                List<Team> teamList = database.teamDao().getAll();
                List<String> teamNames = new ArrayList<>();
                for (Team team : teamList) {
                    teamNames.add(team.name);
                }

                runOnUiThread(() -> {
                    users.clear();
                    users.addAll(userList);
                    userAdapter.clear();
                    userAdapter.addAll(userNames);
                    userAdapter.notifyDataSetChanged();

                    teams.clear();
                    teams.addAll(teamList);
                    teamAdapter.clear();
                    teamAdapter.addAll(teamNames);
                    teamAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void assignUserToTeam() {
        int userPosition = spinnerUsers.getSelectedItemPosition();
        int teamPosition = spinnerTeams.getSelectedItemPosition();

        if (userPosition < 0 || userPosition >= users.size()) {
            Toast.makeText(this, "Vui lòng chọn người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (teamPosition < 0 || teamPosition >= teams.size()) {
            Toast.makeText(this, "Vui lòng chọn ban", Toast.LENGTH_SHORT).show();
            return;
        }

        User selectedUser = users.get(userPosition);
        Team selectedTeam = teams.get(teamPosition);

        executorService.execute(() -> {
            try {
                selectedUser.team_id = selectedTeam.id;
                database.userDao().update(selectedUser);
                
                runOnUiThread(() -> {
                    Toast.makeText(this, "Phân công thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi phân công: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

