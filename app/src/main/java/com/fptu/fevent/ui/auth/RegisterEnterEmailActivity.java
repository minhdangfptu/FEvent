package com.fptu.fevent.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;
import com.fptu.fevent.repository.UserRepository;
import com.fptu.fevent.util.GmailSender;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Random;

public class RegisterEnterEmailActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextInputLayout emailInputLayout;
    private EditText edtEmailInput; // Đổi ID
    private Button btnContinue; // Đổi ID
    private ProgressBar progressBar;
    private UserRepository userRepo;

    private String generatedOtp; // Để lưu OTP được tạo và truyền đi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_enter_email); // Đảm bảo đúng layout

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupEvents();
        userRepo = new UserRepository(getApplication());
    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        emailInputLayout = findViewById(R.id.email_input_layout);
        edtEmailInput = findViewById(R.id.edt_email_input);
        btnContinue = findViewById(R.id.btn_continue);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupEvents() {
        backButton.setOnClickListener(v -> onBackPressed());

        btnContinue.setOnClickListener(v -> {
            String email = edtEmailInput.getText().toString().trim().toLowerCase();

            if (email.isEmpty()) {
                emailInputLayout.setError("Vui lòng nhập email");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.setError("Email không hợp lệ");
            } else {
                emailInputLayout.setError(null);
                showLoading(true);

                // Kiểm tra email có tồn tại chưa (vì đây là đăng ký mới)
                userRepo.isEmailExists(email, exists -> runOnUiThread(() -> {
                    showLoading(false);
                    if (exists) {
                        emailInputLayout.setError("Email này đã được đăng ký. Vui lòng đăng nhập hoặc dùng email khác.");
                    } else {
                        // Email chưa tồn tại, gửi OTP
                        sendOtpForRegistration(email);
                    }
                }));
            }
        });
    }

    private void sendOtpForRegistration(String email) {
        showLoading(true);
        generatedOtp = generateOtp(); // Tạo OTP

        String subject = "Mã xác thực Email để đăng ký tài khoản FEvent";
        String body = "Xin chào,\n\nMã xác thực email của bạn để đăng ký tài khoản là: " + generatedOtp +
                "\n\nMã có hiệu lực trong 10 phút.\n\nFEvent Team.";

        new Thread(() -> {
            try {
                GmailSender.sendEmail(email, subject, body);
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, "Đã gửi mã xác thực đến email: " + email, Toast.LENGTH_LONG).show();

                    // Chuyển sang RegisterConfirmEmailActivity (đã đổi tên thành RegisterConfirmEmailActivity)
                    Intent intent = new Intent(this, RegisterConfirmEmailActivity.class);
                    intent.putExtra(RegisterConfirmEmailActivity.EXTRA_EMAIL, email);
                    intent.putExtra(RegisterConfirmEmailActivity.EXTRA_OTP, generatedOtp);
                    startActivity(intent);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, "Gửi email thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // OTP 6 chữ số
        return String.valueOf(otp);
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnContinue.setEnabled(!isLoading);
        edtEmailInput.setEnabled(!isLoading);
        backButton.setEnabled(!isLoading);
    }
}