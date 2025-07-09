package com.fptu.fevent.ui.team;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.Team;
import com.fptu.fevent.viewmodel.TeamViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TeamListActivity extends AppCompatActivity {

    public static final int ADD_TEAM_REQUEST = 1;

    private TeamViewModel teamViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        setTitle("Quản lý Ban");

        FloatingActionButton fabAddTeam = findViewById(R.id.fab_add_team);
        fabAddTeam.setOnClickListener(v -> {
            // Theo nghiệp vụ, chỉ TBTC mới được tạo ban
            // Logic phân quyền sẽ thêm sau
            Intent intent = new Intent(TeamListActivity.this, AddEditTeamActivity.class);
            startActivityForResult(intent, ADD_TEAM_REQUEST);
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view_team);
        final TeamAdapter adapter = new TeamAdapter();
        recyclerView.setAdapter(adapter);

        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);
        teamViewModel.getAllTeams().observe(this, teams -> {
            adapter.submitList(teams);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_TEAM_REQUEST && resultCode == RESULT_OK && data != null) {
            String name = data.getStringExtra(AddEditTeamActivity.EXTRA_TEAM_NAME);
            String description = data.getStringExtra(AddEditTeamActivity.EXTRA_TEAM_DESCRIPTION);

            Team team = new Team();
            team.name = name;
            team.description = description;
            teamViewModel.insert(team);

            Toast.makeText(this, "Đã lưu ban", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Chưa lưu ban", Toast.LENGTH_SHORT).show();
        }
    }
}