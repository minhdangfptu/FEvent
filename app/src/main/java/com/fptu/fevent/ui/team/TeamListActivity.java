package com.fptu.fevent.ui.team;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.adapter.TeamAdapter;
import com.fptu.fevent.database.AppDatabase;
import com.fptu.fevent.model.Team;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeamListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TeamAdapter teamAdapter;
    private FloatingActionButton fabAddTeam;
    private AppDatabase database;
    private ExecutorService executorService;
    private List<Team> teams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        initViews();
        initDatabase();
        setupRecyclerView();
        setupClickListeners();
        loadTeams();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_teams);
        fabAddTeam = findViewById(R.id.fab_add_team);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        teams = new ArrayList<>();
    }

    private void setupRecyclerView() {
        teamAdapter = new TeamAdapter(this, teams);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(teamAdapter);
    }

    private void setupClickListeners() {
        fabAddTeam.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateTeamActivity.class);
            startActivity(intent);
        });
    }

    private void loadTeams() {
        executorService.execute(() -> {
            try {
                List<Team> teamList = database.teamDao().getAll();
                runOnUiThread(() -> {
                    teams.clear();
                    teams.addAll(teamList);
                    teamAdapter.updateTeams(teams);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tải danh sách ban: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTeams(); // Refresh data when returning to this activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}

