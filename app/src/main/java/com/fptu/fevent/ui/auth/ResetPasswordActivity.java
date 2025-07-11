package com.fptu.fevent.ui.auth;

import android.content.Intent;
import android.os.Bundle;
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
import com.fptu.fevent.repository.UserRepository;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtNewPassword, edtConfirmPassword;
    private TextInputLayout layoutNewPassword, layoutConfirmPassword;
    private Button btnConfirmChange;
    private ImageView backButton;

    private UserRepository userRepo;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // ✅ Thêm insets vào padding hiện có thay vì ghi đè
            v.setPadding(
                    v.getPaddingLeft() + systemBars.left,
                    v.getPaddingTop() + systemBars.top,
                    v.getPaddingRight() + systemBars.right,
                    v.getPaddingBottom() + systemBars.bottom
            );
            return insets;
        });

        initViews();
        userRepo = new UserRepository(getApplication());

        // Nhận email từ intent
        userEmail = getIntent().getStringExtra("email");

        setupEvents();
    }

    private void initViews() {
        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        layoutNewPassword = findViewById(R.id.new_password_layout);
        layoutConfirmPassword = findViewById(R.id.confirm_password_layout);
        btnConfirmChange = findViewById(R.id.btn_confirm_change);
        backButton = findViewById(R.id.back_button);
    }

    private void setupEvents() {
        backButton.setOnClickListener(v -> onBackPressed());

        btnConfirmChange.setOnClickListener(v -> {
            String newPass = edtNewPassword.getText().toString().trim();
            String confirmPass = edtConfirmPassword.getText().toString().trim();

            // Validate
            if (newPass.isEmpty()) {
                layoutNewPassword.setError("Vui lòng nhập mật khẩu mới");
                return;
            } else if (newPass.length() < 6) {
                layoutNewPassword.setError("Vui lòng nhập mật khẩu lớn hơn 6 ký tự");
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

            // Update password
            userRepo.updatePassword(userEmail, newPass, () -> runOnUiThread(() -> {
                Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }));
        });
    }
}
