package com.fptu.fevent.ui.team;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.Team;
import com.fptu.fevent.viewmodel.TeamViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TeamListActivity extends AppCompatActivity {

    public static final int ADD_TEAM_REQUEST = 1;
    public static final int EDIT_TEAM_REQUEST = 2;

    private TeamViewModel teamViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        setTitle("Quản lý Ban");

        FloatingActionButton fabAddTeam = findViewById(R.id.fab_add_team);
        fabAddTeam.setOnClickListener(v -> {
            Intent intent = new Intent(TeamListActivity.this, AddEditTeamActivity.class);
            startActivityForResult(intent, ADD_TEAM_REQUEST);
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view_team);
        final TeamAdapter adapter = new TeamAdapter();
        recyclerView.setAdapter(adapter);

        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);
        teamViewModel.getAllTeams().observe(this, adapter::submitList);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                teamViewModel.delete(adapter.getTeamAt(viewHolder.getAdapterPosition()));
                Toast.makeText(TeamListActivity.this, "Đã xoá ban", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                // **FIX IS HERE:**
                // The 'itemView' variable is part of the 'viewHolder' object and must be accessed from it.
                View itemView = viewHolder.itemView;

                // **TYPO FIX IS HERE:** Corrected "TeamListA"ctivity.this" to "TeamListActivity.this"
                Drawable icon = ContextCompat.getDrawable(TeamListActivity.this, R.drawable.ic_delete_24);
                ColorDrawable background = new ColorDrawable(ContextCompat.getColor(TeamListActivity.this, R.color.delete_red));

                if (dX < 0) { // User is swiping to the left
                    background.setBounds(
                            itemView.getRight() + (int) dX,
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom()
                    );
                    background.draw(c);

                    int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + icon.getIntrinsicHeight();

                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    icon.draw(c);
                }
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(team -> {
            Intent intent = new Intent(TeamListActivity.this, AddEditTeamActivity.class);
            intent.putExtra(AddEditTeamActivity.EXTRA_TEAM_ID, team.id);
            intent.putExtra(AddEditTeamActivity.EXTRA_TEAM_NAME, team.name);
            intent.putExtra(AddEditTeamActivity.EXTRA_TEAM_DESCRIPTION, team.description);
            startActivityForResult(intent, EDIT_TEAM_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        String name = data.getStringExtra(AddEditTeamActivity.EXTRA_TEAM_NAME);
        String description = data.getStringExtra(AddEditTeamActivity.EXTRA_TEAM_DESCRIPTION);

        if (requestCode == ADD_TEAM_REQUEST) {
            Team team = new Team();
            team.name = name;
            team.description = description;
            teamViewModel.insert(team);
            Toast.makeText(this, "Đã lưu ban", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_TEAM_REQUEST) {
            int id = data.getIntExtra(AddEditTeamActivity.EXTRA_TEAM_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Không thể cập nhật", Toast.LENGTH_SHORT).show();
                return;
            }
            Team team = new Team();
            team.id = id;
            team.name = name;
            team.description = description;
            teamViewModel.update(team);
            Toast.makeText(this, "Đã cập nhật ban", Toast.LENGTH_SHORT).show();
        }
    }
}