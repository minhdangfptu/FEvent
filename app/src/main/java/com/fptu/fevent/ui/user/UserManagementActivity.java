package com.fptu.fevent.ui.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.fptu.fevent.R;
import com.fptu.fevent.repository.UserRepository;
import com.fptu.fevent.ui.auth.ChangePasswordActivity;
import com.fptu.fevent.ui.auth.LoginActivity;
import com.fptu.fevent.ui.common.HomeActivity;

import java.util.Calendar;

public class UserManagementActivity extends AppCompatActivity {
    private TextView tvUserName, tvUserEmail;
    private ImageView profileImage;
    private ProgressBar progressBarImage; // Thêm ProgressBar
    private UserRepository userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management);

        // Ánh xạ View
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        profileImage = findViewById(R.id.profile_image);
        progressBarImage = findViewById(R.id.progress_bar_image); // Ánh xạ ProgressBar

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

        // --- HIỂN THỊ ẢNH VỚI GLIDE VÀ LOADING ---
        if (!avatarUrl.isEmpty()) {
            progressBarImage.setVisibility(View.VISIBLE); // Hiển thị ProgressBar khi bắt đầu tải
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.fu_login) // Ảnh placeholder khi đang tải
                    .error(R.drawable.fu_login)      // Ảnh khi có lỗi tải
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            progressBarImage.setVisibility(View.GONE); // Ẩn ProgressBar khi lỗi
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            progressBarImage.setVisibility(View.GONE); // Ẩn ProgressBar khi tải thành công
                            return false;
                        }
                    })
                    .into(profileImage);
        } else {
            // Nếu không có URL, hiển thị ảnh mặc định và ẩn ProgressBar
            profileImage.setImageResource(R.drawable.fu_login);
            progressBarImage.setVisibility(View.GONE);
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