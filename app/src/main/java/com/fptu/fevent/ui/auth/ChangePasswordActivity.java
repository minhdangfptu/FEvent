package com.fptu.fevent.ui.auth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;
import com.fptu.fevent.model.User;
import com.fptu.fevent.repository.UserRepository;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private TextInputLayout layoutOldPassword, layoutNewPassword, layoutConfirmPassword;
    private Button btnConfirmChange;
    private ImageView backButton;

    private UserRepository userRepo;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        View mainView = findViewById(R.id.main);

        int originalPaddingLeft = mainView.getPaddingLeft();
        int originalPaddingTop = mainView.getPaddingTop();
        int originalPaddingRight = mainView.getPaddingRight();
        int originalPaddingBottom = mainView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    originalPaddingLeft + systemBars.left,
                    originalPaddingTop + systemBars.top,
                    originalPaddingRight + systemBars.right,
                    originalPaddingBottom + systemBars.bottom
            );
            return insets;
        });

        initViews();
        setupEvents();
        userRepo = new UserRepository(getApplication());

        // ✅ Lấy email từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userEmail = prefs.getString("email", null);
    }

    private void initViews() {
        edtOldPassword = findViewById(R.id.edt_old_password);
        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        layoutOldPassword = findViewById(R.id.old_password_layout);
        layoutNewPassword = findViewById(R.id.new_password_layout);
        layoutConfirmPassword = findViewById(R.id.confirm_password_layout);
        btnConfirmChange = findViewById(R.id.btn_confirm_change);
        backButton = findViewById(R.id.back_button);
    }

    private void setupEvents() {
        backButton.setOnClickListener(v -> onBackPressed());

        btnConfirmChange.setOnClickListener(v -> {
            String oldPass = edtOldPassword.getText().toString().trim();
            String newPass = edtNewPassword.getText().toString().trim();
            String confirmPass = edtConfirmPassword.getText().toString().trim();

            if (oldPass.isEmpty()) {
                layoutOldPassword.setError("Nhập mật khẩu cũ");
                return;
            } else {
                layoutOldPassword.setError(null);
            }

            if (newPass.isEmpty()) {
                layoutNewPassword.setError("Nhập mật khẩu mới");
                return;
            } else {
                layoutNewPassword.setError(null);
            }

            if (!newPass.equals(confirmPass)) {
                layoutConfirmPassword.setError("Mật khẩu xác nhận không khớp");
                return;
            } else {
                layoutConfirmPassword.setError(null);
            }

            // Kiểm tra mật khẩu cũ hợp lệ
            userRepo.login(userEmail, oldPass, user -> runOnUiThread(() -> {
                if (user == null) {
                    layoutOldPassword.setError("Mật khẩu cũ không đúng");
                } else {
                    // Cập nhật mật khẩu
                    userRepo.updatePassword(userEmail, newPass, () -> runOnUiThread(() -> {
                        Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        finish(); // Hoặc chuyển hướng
                    }));
                }
            }));
        });
    }
}
