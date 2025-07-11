package com.fptu.fevent.ui.auth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
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
import com.fptu.fevent.repository.UserRepository;
import com.google.android.material.textfield.TextInputLayout;

public class ChangeEmailActivity extends AppCompatActivity {

    private TextInputLayout oldPasswordLayout, newEmailLayout, confirmEmailLayout;
    private EditText edtOldPassword, edtNewEmail, edtConfirmEmail;
    private Button btnConfirm;
    private ImageView backButton;
    private String userEmail;
    private UserRepository userRepo;
    private String currentEmail; // truyền từ SharedPref hoặc Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_email); // bạn sẽ dùng layout đã có

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
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentEmail = prefs.getString("email", null);
    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        edtOldPassword = findViewById(R.id.edt_old_password);
        edtNewEmail = findViewById(R.id.edt_new_password);
        edtConfirmEmail = findViewById(R.id.edt_confirm_password);

        oldPasswordLayout = findViewById(R.id.old_password_layout);
        newEmailLayout = findViewById(R.id.new_password_layout);
        confirmEmailLayout = findViewById(R.id.confirm_password_layout);
        btnConfirm = findViewById(R.id.btn_confirm_change);
    }

    private void setupEvents() {
        backButton.setOnClickListener(v -> onBackPressed());

        btnConfirm.setOnClickListener(v -> {
            String password = edtOldPassword.getText().toString().trim();
            String newEmail = edtNewEmail.getText().toString().trim();
            String confirmEmail = edtConfirmEmail.getText().toString().trim();

            boolean valid = true;

            if (password.isEmpty()) {
                oldPasswordLayout.setError("Vui lòng nhập mật khẩu");
                valid = false;
            } else {
                oldPasswordLayout.setError(null);
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                newEmailLayout.setError("Email mới không hợp lệ");
                valid = false;
            } else {
                newEmailLayout.setError(null);
            }

            if (!newEmail.equals(confirmEmail)) {
                confirmEmailLayout.setError("Email xác nhận không khớp");
                valid = false;
            } else {
                confirmEmailLayout.setError(null);
            }

            if (!valid) return;

            // Bước 1: Kiểm tra email mới đã tồn tại chưa
            userRepo.isEmailExists(newEmail, exists -> runOnUiThread(() -> {
                if (exists) {
                    newEmailLayout.setError("Email không khả dụng");
                } else {
                    newEmailLayout.setError(null);

                    // Bước 2: Kiểm tra mật khẩu hợp lệ
                    userRepo.login(currentEmail, password, user -> runOnUiThread(() -> {
                        if (user == null) {
                            oldPasswordLayout.setError("Mật khẩu không đúng");
                        } else {
                            oldPasswordLayout.setError(null);

                            // Bước 3: Cập nhật email
                            userRepo.updateEmail(currentEmail, newEmail, () -> runOnUiThread(() -> {
                                Toast.makeText(this, "Cập nhật email thành công", Toast.LENGTH_SHORT).show();

                                // Cập nhật SharedPreferences nếu cần
                                SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
                                editor.putString("email", newEmail);
                                editor.apply();

                                finish(); // hoặc chuyển về màn hình chính
                            }));
                        }
                    }));
                }
            }));
        });
    }

}
