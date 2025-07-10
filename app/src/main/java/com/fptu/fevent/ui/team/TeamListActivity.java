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
    // Hằng số EDIT_TEAM_REQUEST không còn được dùng trực tiếp ở đây nữa,
    // nhưng vẫn có thể giữ lại để tiện cho việc phát triển sau này.
    public static final int EDIT_TEAM_REQUEST = 2;

    private TeamViewModel teamViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        setTitle("Quản lý Ban");

        // Nút bấm để mở màn hình Thêm ban (vẫn giữ nguyên)
        FloatingActionButton fabAddTeam = findViewById(R.id.fab_add_team);
        fabAddTeam.setOnClickListener(v -> {
            Intent intent = new Intent(TeamListActivity.this, AddEditTeamActivity.class);
            // Chúng ta vẫn dùng startActivityForResult cho trường hợp THÊM để có thể hiện Toast
            startActivityForResult(intent, ADD_TEAM_REQUEST);
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view_team);
        final TeamAdapter adapter = new TeamAdapter();
        recyclerView.setAdapter(adapter);

        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);
        teamViewModel.getAllTeams().observe(this, adapter::submitList);

        // Chức năng vuốt để xóa (vẫn giữ nguyên)
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
                View itemView = viewHolder.itemView;
                Drawable icon = ContextCompat.getDrawable(TeamListActivity.this, R.drawable.ic_delete_24);
                ColorDrawable background = new ColorDrawable(ContextCompat.getColor(TeamListActivity.this, R.color.red_500));
                if (dX < 0) {
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
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

        // ==========================================================
        // ===       THAY ĐỔI QUAN TRỌNG CHO GIAI ĐOẠN 3          ===
        // ==========================================================
        // Khi người dùng click vào một item, mở màn hình CHI TIẾT thay vì màn hình SỬA
        adapter.setOnItemClickListener(team -> {
            Intent intent = new Intent(TeamListActivity.this, TeamDetailActivity.class);
            intent.putExtra(TeamDetailActivity.EXTRA_TEAM_ID, team.id);
            intent.putExtra(TeamDetailActivity.EXTRA_TEAM_NAME, team.name);
            intent.putExtra(TeamDetailActivity.EXTRA_TEAM_DESCRIPTION, team.description);
            startActivity(intent); // Dùng startActivity vì không cần nhận kết quả trả về
        });
    }

    /**
     * Phương thức này chỉ còn xử lý kết quả trả về từ việc THÊM MỚI ban.
     * Việc SỬA ban sẽ được thực hiện bên trong TeamDetailActivity trong tương lai.
     * Toàn bộ phương thức này đã được COMMENT LẠI vì hiện tại không còn cần thiết,
     * nhưng bạn có thể mở ra để xử lý Toast cho trường hợp Thêm mới nếu muốn.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_TEAM_REQUEST && resultCode == RESULT_OK) {
            // Lấy dữ liệu từ màn hình Thêm mới và tạo Team
            String name = data.getStringExtra(AddEditTeamActivity.EXTRA_TEAM_NAME);
            String description = data.getStringExtra(AddEditTeamActivity.EXTRA_TEAM_DESCRIPTION);

            Team team = new Team();
            team.name = name;
            team.description = description;
            teamViewModel.insert(team);

            Toast.makeText(this, "Đã tạo ban thành công", Toast.LENGTH_SHORT).show();
        }

        // Không cần xử lý EDIT_TEAM_REQUEST ở đây nữa
    }
}