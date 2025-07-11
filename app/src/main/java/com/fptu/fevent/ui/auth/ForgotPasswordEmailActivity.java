package com.fptu.fevent.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import com.fptu.fevent.util.GmailSender;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Random;

public class ForgotPasswordEmailActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextInputLayout emailInputLayout;
    private EditText edtEmail;
    private Button btnSendCode;
    private UserRepository userRepo;

    private String otpCode; // Lưu OTP để chuyển qua activity sau (nếu cần)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_email);

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
        edtEmail = findViewById(R.id.edt_email);
        btnSendCode = findViewById(R.id.btn_send_code);
    }

    private void setupEvents() {
        backButton.setOnClickListener(v -> onBackPressed());

        btnSendCode.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            if (email.isEmpty()) {
                emailInputLayout.setError("Vui lòng nhập email");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.setError("Email không hợp lệ");
            } else {
                emailInputLayout.setError(null);

                userRepo.isEmailExists(email, exists -> runOnUiThread(() -> {
                    if (!exists) {
                        emailInputLayout.setError("Email không tồn tại trong hệ thống");
                    } else {
                        emailInputLayout.setError(null);
                        sendOtpToEmail(email);
                    }
                }));
            }
        });
    }

    private void sendOtpToEmail(String email) {
        otpCode = generateOtp();
        String subject = "Xác thực đặt lại mật khẩu";
        String body = "Xin chào,\n\nMã xác thực đặt lại mật khẩu của bạn là: " + otpCode +
                "\n\nMã có hiệu lực trong 10 phút.\n\nFEvent Team.";

        new Thread(() -> {
            try {
                GmailSender.sendEmail(email, subject, body);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã gửi mã xác thực đến email: " + email, Toast.LENGTH_LONG).show();

                    // 👉 Nếu bạn muốn chuyển sang VerifyOtpActivity:
                    Intent intent = new Intent(this, ForgotVerifyActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("otp", otpCode);
                    startActivity(intent);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Gửi email thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // OTP 6 chữ số
        return String.valueOf(otp);
    }
}
