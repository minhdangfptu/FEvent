package com.fptu.fevent.ui.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView; // Import ImageView
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide; // Import Glide
import com.fptu.fevent.R;
import com.fptu.fevent.repository.UserRepository;
import com.fptu.fevent.ui.auth.ChangePasswordActivity; // Giữ nếu có dùng, nếu không thì xóa
import com.fptu.fevent.ui.auth.LoginActivity;
import com.fptu.fevent.ui.common.HomeActivity; // Giữ nếu có dùng, nếu không thì xóa

import java.util.Calendar;

public class UserManagementActivity extends AppCompatActivity {
    private TextView tvUserName, tvUserEmail;
    private ImageView profileImage; // Khai báo ImageView
    private UserRepository userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management);

        // Ánh xạ View
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        profileImage = findViewById(R.id.profile_image); // Ánh xạ ImageView

        userRepo = new UserRepository(getApplication());

        // Lấy dữ liệu từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int user_id = prefs.getInt("user_id", 0);
        String fullname = prefs.getString("fullname", "Tên người dùng");
        String email = prefs.getString("email", "Email chưa xác định");
        String avatarUrl = prefs.getString("avatar", ""); // Lấy URL ảnh từ SharedPreferences

        // Gán vào TextView
        tvUserName.setText(fullname);
        tvUserEmail.setText(email);

        // --- HIỂN THỊ ẢNH VỚI GLIDE ---
        if (!avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.fu_login) // Ảnh placeholder khi đang tải hoặc lỗi
                    .error(R.drawable.fu_login)      // Ảnh khi có lỗi tải
                    .into(profileImage);
        } else {
            // Nếu không có URL, hiển thị ảnh mặc định
            profileImage.setImageResource(R.drawable.fu_login);
        }
        // -----------------------------

        // Ánh xạ View và xử lý sự kiện
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btn_settings).setOnClickListener(v -> {
            Toast.makeText(this, "Cài đặt ứng dụng", Toast.LENGTH_SHORT).show();
            // TODO: Mở màn hình cài đặt nếu cần
        });

        findViewById(R.id.menu_favourites).setOnClickListener(v -> {
            navigateUserProfile();
        });

        findViewById(R.id.menu_downloads).setOnClickListener(v -> {
            navigateUserEditProfile();
        });
        findViewById(R.id.btn_edit_profile).setOnClickListener(v -> {
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
                        prefs.edit().remove("user_id").apply(); // Có thể cần xóa thêm các thông tin user khác như fullname, email, avatar_url

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
                                    prefs.edit().remove("user_id").apply(); // Xóa tất cả thông tin user khỏi SharedPreferences

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
            getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply(); // Xóa toàn bộ prefs của user
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