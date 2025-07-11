package com.fptu.fevent.ui.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;
import com.fptu.fevent.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvFullname, tvDob, tvClub, tvDepartment, tvPosition, tvRole, tvTeam, tv_user_name1, tvPhoneNum;
    private ImageView btnBack;
    private Button btnEdit;

    private UserRepository userRepo;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        userRepo = new UserRepository(getApplication());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadUser();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnEdit = findViewById(R.id.btn_edit_profile);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvFullname = findViewById(R.id.tv_fullname);
        tvPhoneNum = findViewById(R.id.tv_phoneNum);
        tvDob = findViewById(R.id.tv_dob);
        tvClub = findViewById(R.id.tv_club);
        tvDepartment = findViewById(R.id.tv_department);
        tvPosition = findViewById(R.id.tv_position);
        tvRole = findViewById(R.id.tv_role);
        tvTeam = findViewById(R.id.tv_team);
        tv_user_name1 = findViewById(R.id.tv_user_name1);
        btnBack.setOnClickListener(v -> onBackPressed());
        btnEdit.setOnClickListener(v -> navigateUserEditProfile());
    }

    private void loadUser() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy user_id", Toast.LENGTH_SHORT).show();
            return;
        }

        userRepo.getUserDetailsWithNames(userId, user -> {
            if (user == null) {
                runOnUiThread(() -> Toast.makeText(this, "Không có người dùng", Toast.LENGTH_SHORT).show());
                return;
            }

            runOnUiThread(() -> {
                tv_user_name1.setText(user.name);
                tvUserName.setText(user.name);
                tvUserEmail.setText(user.email);
                tvFullname.setText(getSafe(user.fullname));
                tvDob.setText(formatDate(user.date_of_birth));
                tvPhoneNum.setText(getSafe(user.phone_number));
                tvClub.setText(getSafe(user.club));
                tvDepartment.setText(getSafe(user.department));
                tvPosition.setText(getSafe(user.position));
                tvRole.setText(getSafe(user.roleName));
                tvTeam.setText(getSafe(user.teamName));
            });
        });

    }

    private String getSafe(Object value) {
        return value != null ? value.toString() : "N/A";
    }

    private String formatDate(Date date) {
        return date != null ? dateFormat.format(date) : "N/A";
    }
    private void navigateUserEditProfile() {
        Intent intent = new Intent(UserProfileActivity.this, UserEditProfileActivity.class);
        startActivity(intent);
    }
}
