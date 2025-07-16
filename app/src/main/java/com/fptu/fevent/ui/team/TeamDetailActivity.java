package com.fptu.fevent.ui.team;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.adapter.UserAdapter;
import com.fptu.fevent.database.AppDatabase;
import com.fptu.fevent.model.Team;
import com.fptu.fevent.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeamDetailActivity extends AppCompatActivity {
    private TextView tvTeamName, tvTeamDescription, tvMemberCount;
    private RecyclerView recyclerViewMembers;
    private UserAdapter userAdapter;
    private AppDatabase database;
    private ExecutorService executorService;
    private int teamId;
    private Team currentTeam;
    private List<User> teamMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);

        teamId = getIntent().getIntExtra("team_id", -1);
        if (teamId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin ban", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initDatabase();
        setupRecyclerView();
        loadTeamData();
    }

    private void initViews() {
        tvTeamName = findViewById(R.id.tv_team_name);
        tvTeamDescription = findViewById(R.id.tv_team_description);
        tvMemberCount = findViewById(R.id.tv_member_count);
        recyclerViewMembers = findViewById(R.id.recycler_view_members);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        teamMembers = new ArrayList<>();
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(this, teamMembers);
        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMembers.setAdapter(userAdapter);
    }

    private void loadTeamData() {
        executorService.execute(() -> {
            try {
                // Load team info
                currentTeam = database.teamDao().getById(teamId);
                
                // Load team members
                List<User> members = database.userDao().getUsersByTeamId(teamId);
                
                runOnUiThread(() -> {
                    if (currentTeam != null) {
                        tvTeamName.setText(currentTeam.name);
                        tvTeamDescription.setText(currentTeam.description);
                        tvMemberCount.setText("Số thành viên: " + members.size());
                        
                        teamMembers.clear();
                        teamMembers.addAll(members);
                        userAdapter.updateUsers(teamMembers);
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin ban", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tải thông tin ban: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

