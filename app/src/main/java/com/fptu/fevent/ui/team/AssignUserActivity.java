package com.fptu.fevent.ui.team;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.User;
import com.fptu.fevent.viewmodel.UserViewModel;

import java.util.Set;

public class AssignUserActivity extends AppCompatActivity {

    public static final String EXTRA_TEAM_ID_ASSIGN = "com.fptu.fevent.EXTRA_TEAM_ID_ASSIGN";
    private UserViewModel userViewModel;
    private AssignUserAdapter adapter;
    private int teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_FEvent_NoActionBar);
        setContentView(R.layout.activity_assign_user);

        Toolbar toolbar = findViewById(R.id.toolbar_assign_user);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Phân công thành viên");
        }

        Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_TEAM_ID_ASSIGN)) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ban", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        teamId = intent.getIntExtra(EXTRA_TEAM_ID_ASSIGN, -1);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_assign_user);
        adapter = new AssignUserAdapter();
        recyclerView.setAdapter(adapter);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUnassignedUsers().observe(this, adapter::submitList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.assign_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save_assignment) {
            saveAssignments();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAssignments() {
        Set<User> selectedUsers = adapter.getSelectedUsers();
        if (selectedUsers.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một thành viên", Toast.LENGTH_SHORT).show();
            return;
        }

        for (User user : selectedUsers) {
            user.team_id = teamId;
            userViewModel.updateUser(user);
        }

        Toast.makeText(this, "Đã thêm " + selectedUsers.size() + " thành viên", Toast.LENGTH_SHORT).show();
        finish();
    }
}