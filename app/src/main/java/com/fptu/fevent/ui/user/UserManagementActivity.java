package com.fptu.fevent.ui.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;
import com.fptu.fevent.repository.UserRepository;
import com.fptu.fevent.ui.auth.LoginActivity;
import com.fptu.fevent.ui.common.HomeActivity;

import java.util.Calendar;

public class UserManagementActivity extends AppCompatActivity {
    private TextView tvUserName, tvUserEmail;
    private UserRepository userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management);
        // Ánh xạ View
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        userRepo = new UserRepository(getApplication());
        // Lấy dữ liệu từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int user_id = prefs.getInt("user_id", 0);
        String fullname = prefs.getString("fullname", "Tên người dùng");
        String email = prefs.getString("email", "Email chưa xác định");

        // Gán vào TextView
        tvUserName.setText(fullname);
        tvUserEmail.setText(email);
        // Ánh xạ View và xử lý sự kiện
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btn_settings).setOnClickListener(v -> {
            Toast.makeText(this, "Cài đặt ứng dụng", Toast.LENGTH_SHORT).show();
            // TODO: Mở màn hình cài đặt nếu cần
        });

        findViewById(R.id.menu_favourites).setOnClickListener(v -> {
//            Toast.makeText(this, "Xem thông tin chi tiết tài khoản", Toast.LENGTH_SHORT).show();
            navigateUserProfile();
        });

        findViewById(R.id.menu_downloads).setOnClickListener(v -> {
//            Toast.makeText(this, "Chỉnh sửa thông tin tài khoản", Toast.LENGTH_SHORT).show();
            navigateUserEditProfile();
        });
        findViewById(R.id.btn_edit_profile).setOnClickListener(v -> {
//            Toast.makeText(this, "Chỉnh sửa thông tin tài khoản", Toast.LENGTH_SHORT).show();
            navigateUserEditProfile();
        });


        findViewById(R.id.menu_language).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn vô hiệu hóa tài khoản trong 30 ngày?")
                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                        // Cập nhật thời gian vô hiệu hóa
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_MONTH, 30);
                        userRepo.deactivateUser(user_id, cal.getTime());

                        // Xóa user_id và logout
                        prefs.edit().remove("user_id").apply();

                        Toast.makeText(this, "Tài khoản đã bị vô hiệu hóa 30 ngày", Toast.LENGTH_SHORT).show();

                        // Chuyển về màn hình đăng nhập
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });


        findViewById(R.id.menu_display).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xóa tài khoản")
                    .setMessage("Bạn có chắc chắn muốn xóa tài khoản? Hành động này không thể hoàn tác.")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        if (user_id != -1) {
                            userRepo.deleteUserById(user_id, success -> runOnUiThread(() -> {
                                if (success) {
                                    prefs.edit().remove("user_id").apply();

                                    Toast.makeText(this, "Tài khoản đã được xóa", Toast.LENGTH_SHORT).show();
                                    // Chuyển về màn hình đăng nhập
                                    Intent intent = new Intent(this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(this, "Xóa tài khoản thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }));
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        findViewById(R.id.menu_logout).setOnClickListener(v -> {
            Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            // Clear session & quay lại LoginActivity
            getSharedPreferences("user_session", MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(UserManagementActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Áp dụng padding tránh status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void navigateUserProfile() {
        Intent intent = new Intent(UserManagementActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

    private void navigateUserEditProfile() {
        Intent intent = new Intent(UserManagementActivity.this, UserEditProfileActivity.class);
        startActivity(intent);
    }
}
