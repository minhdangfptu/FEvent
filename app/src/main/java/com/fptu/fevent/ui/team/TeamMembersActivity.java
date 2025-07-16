package com.fptu.fevent.ui.team;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.fevent.R;
import com.fptu.fevent.database.AppDatabase;
import com.fptu.fevent.model.User;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeamMembersActivity extends AppCompatActivity {
    private TextView tvUserName, tvUserEmail, tvUserPhone, tvUserDob, 
                     tvUserClub, tvUserDepartment, tvUserPosition;
    private AppDatabase database;
    private ExecutorService executorService;
    private int userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_members);

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin thành viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initDatabase();
        loadUserData();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvUserPhone = findViewById(R.id.tv_user_phone);
        tvUserDob = findViewById(R.id.tv_user_dob);
        tvUserClub = findViewById(R.id.tv_user_club);
        tvUserDepartment = findViewById(R.id.tv_user_department);
        tvUserPosition = findViewById(R.id.tv_user_position);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
    }

    private void loadUserData() {
        executorService.execute(() -> {
            try {
                currentUser = database.userDao().getById(userId);
                if (currentUser != null) {
                    runOnUiThread(() -> {
                        displayUserInfo();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Không tìm thấy thông tin thành viên", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tải thông tin thành viên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void displayUserInfo() {
        tvUserName.setText(currentUser.fullname != null ? currentUser.fullname : "N/A");
        tvUserEmail.setText(currentUser.email != null ? currentUser.email : "N/A");
        tvUserPhone.setText(currentUser.phone_number != null ? currentUser.phone_number : "N/A");
        
        if (currentUser.date_of_birth != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvUserDob.setText(sdf.format(currentUser.date_of_birth));
        } else {
            tvUserDob.setText("N/A");
        }
        
        tvUserClub.setText(currentUser.club != null ? currentUser.club : "N/A");
        tvUserDepartment.setText(currentUser.department != null ? currentUser.department : "N/A");
        tvUserPosition.setText(currentUser.position != null ? currentUser.position : "N/A");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}

