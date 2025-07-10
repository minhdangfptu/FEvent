package com.fptu.fevent.ui.team;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.Team;
import com.fptu.fevent.viewmodel.TeamViewModel;
import com.fptu.fevent.viewmodel.UserViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TeamDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TEAM_ID = "com.fptu.fevent.EXTRA_TEAM_ID_DETAIL";
    public static final String EXTRA_TEAM_NAME = "com.fptu.fevent.EXTRA_TEAM_NAME_DETAIL";
    public static final String EXTRA_TEAM_DESCRIPTION = "com.fptu.fevent.EXTRA_TEAM_DESCRIPTION_DETAIL";

    private UserViewModel userViewModel;
    private TeamViewModel teamViewModel;
    private UserAdapter adapter;

    private int teamId;
    private String teamName;
    private String teamDescription;

    private ActivityResultLauncher<Intent> editTeamLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_FEvent_NoActionBar);
        setContentView(R.layout.activity_team_detail);

        Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_TEAM_ID)) {
            Toast.makeText(this, "Không tìm thấy thông tin ban", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        teamId = intent.getIntExtra(EXTRA_TEAM_ID, -1);
        teamName = intent.getStringExtra(EXTRA_TEAM_NAME);
        teamDescription = intent.getStringExtra(EXTRA_TEAM_DESCRIPTION);

        Toolbar toolbar = findViewById(R.id.toolbar_team_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết Ban");
        }

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_team_detail);
        adapter = new UserAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setHeaderData(teamName, teamDescription);

        userViewModel.getUsersInTeam(teamId).observe(this, adapter::submitList);

        FloatingActionButton fabAddMember = findViewById(R.id.fab_add_member);
        fabAddMember.setOnClickListener(v -> {
            Intent assignIntent = new Intent(TeamDetailActivity.this, AssignUserActivity.class);
            assignIntent.putExtra(AssignUserActivity.EXTRA_TEAM_ID_ASSIGN, teamId);
            startActivity(assignIntent);
        });

        handleEditResult();
    }

    private void handleEditResult() {
        editTeamLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        teamName = data.getStringExtra(AddEditTeamActivity.EXTRA_TEAM_NAME);
                        teamDescription = data.getStringExtra(AddEditTeamActivity.EXTRA_TEAM_DESCRIPTION);

                        adapter.setHeaderData(teamName, teamDescription);

                        Team updatedTeam = new Team();
                        updatedTeam.id = teamId;
                        updatedTeam.name = teamName;
                        updatedTeam.description = teamDescription;
                        teamViewModel.update(updatedTeam);

                        Toast.makeText(this, "Đã cập nhật ban", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.team_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_edit_team) {
            Intent intent = new Intent(this, AddEditTeamActivity.class);
            intent.putExtra(AddEditTeamActivity.EXTRA_TEAM_ID, teamId);
            intent.putExtra(AddEditTeamActivity.EXTRA_TEAM_NAME, teamName);
            intent.putExtra(AddEditTeamActivity.EXTRA_TEAM_DESCRIPTION, teamDescription);
            editTeamLauncher.launch(intent);
            return true;
        } else if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}