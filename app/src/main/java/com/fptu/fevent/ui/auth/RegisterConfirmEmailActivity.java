package com.fptu.fevent.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;
import com.fptu.fevent.repository.UserRepository; // Still needed for potential future use or if you had other user-related actions here.
import com.fptu.fevent.util.GmailSender;

import java.util.Random;

public class RegisterConfirmEmailActivity extends AppCompatActivity {

    // Renaming EXTRA_EMAIL to make it more descriptive and align with standard practice.
    // However, if you already have RegisterActivity.EXTRA_REGISTER_EMAIL,
    // and RegisterEnterEmailActivity sends it as EXTRA_EMAIL, let's keep it consistent with the sending side.
    // For clarity, I'll use EXTRA_EMAIL and EXTRA_OTP as originally defined.
    public static final String EXTRA_EMAIL = "extra_email"; // This key should match what RegisterEnterEmailActivity sends.
    public static final String EXTRA_OTP = "extra_otp";

    private ImageView backButton;
    private TextView tvEmailDisplayOtp, tvResendCode, tvTitle, tvDescription;
    private EditText edtOtp;
    private Button btnVerifyOtp;
    private ProgressBar progressBarOtp;

    private String userEmail;
    private String expectedOtp;
    // Removed isForRegistration as it's no longer needed.
    private CountDownTimer resendTimer;
    private static final long OTP_RESEND_DELAY_MILLIS = 30 * 1000;
    private long currentMillisUntilFinished = 0;
    private UserRepository userRepo; // Kept as you might use it for other user-related checks if needed, though not directly for email confirmation status here.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_confirm_email); // Ensure this XML layout is used.

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupEvents();
        userRepo = new UserRepository(getApplication()); // Initialize UserRepository

        // Get data from Intent
        userEmail = getIntent().getStringExtra(EXTRA_EMAIL);
        expectedOtp = getIntent().getStringExtra(EXTRA_OTP);

        if (userEmail != null && !userEmail.isEmpty()) {
            tvEmailDisplayOtp.setText(userEmail);
        } else {
            Toast.makeText(this, "Không tìm thấy địa chỉ email.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (expectedOtp == null || expectedOtp.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy mã OTP. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
        } else {
            // For debugging only. Remove in production.
            Toast.makeText(this, "Mã OTP đã gửi: " + expectedOtp, Toast.LENGTH_LONG).show();
        }

        // Set UI texts specifically for registration flow
        tvTitle.setText("Xác nhận Email đăng ký");
        tvDescription.setText("Chúng tôi đã gửi mã xác thực đến email của bạn để tiếp tục đăng ký:");
        btnVerifyOtp.setText("Tiếp tục đăng ký");

        startResendTimer();
    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        tvTitle = findViewById(R.id.tv_title);
        tvDescription = findViewById(R.id.tv_description);
        tvEmailDisplayOtp = findViewById(R.id.tv_email_display_otp);
        edtOtp = findViewById(R.id.edt_otp);
        btnVerifyOtp = findViewById(R.id.btn_verify_otp);
        tvResendCode = findViewById(R.id.tv_resend_code);
        progressBarOtp = findViewById(R.id.progressBarOtp);
    }

    private void setupEvents() {
        backButton.setOnClickListener(v -> onBackPressed());
        btnVerifyOtp.setOnClickListener(v -> verifyOtp());
        tvResendCode.setOnClickListener(v -> {
            if (tvResendCode.isEnabled()) {
                sendVerificationCode(userEmail);
            }
        });
    }

    private void sendVerificationCode(String email) {
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Không thể gửi lại mã. Địa chỉ email không hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        tvResendCode.setEnabled(false);

        // Generate a new OTP when resending
        expectedOtp = generateOtp();
        String subject = "Mã xác thực Email để đăng ký tài khoản (Gửi lại)";
        String body = "Xin chào,\n\nMã xác thực email của bạn để đăng ký tài khoản mới là: " + expectedOtp +
                "\n\nMã có hiệu lực trong 10 phút.\n\nFEvent Team.";

        new Thread(() -> {
            try {
                GmailSender.sendEmail(email, subject, body);
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, "Mã xác thực mới đã được gửi lại.", Toast.LENGTH_LONG).show();
                    startResendTimer();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, "Gửi email thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    tvResendCode.setEnabled(true);
                });
            }
        }).start();
    }

    private void verifyOtp() {
        String enteredOtp = edtOtp.getText().toString().trim();

        if (TextUtils.isEmpty(enteredOtp)) {
            Toast.makeText(this, "Vui lòng nhập mã xác thực", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expectedOtp == null || expectedOtp.isEmpty()) {
            Toast.makeText(this, "Chưa có mã xác thực để kiểm tra. Vui lòng yêu cầu gửi mã.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        if (enteredOtp.equals(expectedOtp)) {
            // OTP is correct for registration, proceed to RegisterActivity to complete registration
            runOnUiThread(() -> {
                showLoading(false);
                Toast.makeText(RegisterConfirmEmailActivity.this, "Xác thực email thành công! Vui lòng hoàn tất đăng ký.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterConfirmEmailActivity.this, RegisterActivity.class);
                intent.putExtra(RegisterActivity.EXTRA_REGISTER_EMAIL, userEmail); // Pass the confirmed email
                startActivity(intent);
                finish();
            });
        } else {
            // Incorrect OTP
            runOnUiThread(() -> {
                showLoading(false);
                Toast.makeText(RegisterConfirmEmailActivity.this, "Mã xác thực không đúng. Vui lòng kiểm tra lại.", Toast.LENGTH_LONG).show();
            });
        }
    }

    private void showLoading(boolean isLoading) {
        progressBarOtp.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnVerifyOtp.setEnabled(!isLoading);
        edtOtp.setEnabled(!isLoading);
        backButton.setEnabled(!isLoading);
        // Logic cho tvResendCode đã được đơn giản hóa, chỉ phụ thuộc vào trạng thái isLoading
        // và việc timer đã hết hay chưa.
        // Nó sẽ được bật/tắt rõ ràng hơn bởi hàm startResendTimer và onFinish.
        if (isLoading) {
            tvResendCode.setEnabled(false);
        } else {
            // Khi không loading, trạng thái của tvResendCode sẽ được quản lý bởi timer (onFinish/onTick)
            // hoặc khi mới khởi tạo, nó sẽ mặc định là enabled nếu chưa có timer chạy.
            // Điều này tránh trường hợp nó bị enable khi đang có timer chạy.
        }
    }

    private void startResendTimer() {
        if (resendTimer != null) {
            resendTimer.cancel();
        }
        resendTimer = new CountDownTimer(OTP_RESEND_DELAY_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentMillisUntilFinished = millisUntilFinished; // Cập nhật biến này
                long secondsLeft = millisUntilFinished / 1000;
                tvResendCode.setText("Gửi lại sau " + secondsLeft + "s");
                tvResendCode.setEnabled(false); // Luôn tắt trong khi đếm ngược
                tvResendCode.setTextColor(ContextCompat.getColor(RegisterConfirmEmailActivity.this, R.color.gray_500));
            }

            @Override
            public void onFinish() {
                currentMillisUntilFinished = 0; // Đặt về 0 khi kết thúc
                tvResendCode.setText("Không nhận được mã? Gửi lại");
                tvResendCode.setEnabled(true); // Bật khi đếm ngược kết thúc
                tvResendCode.setTextColor(ContextCompat.getColor(RegisterConfirmEmailActivity.this, R.color.blue_500));
            }
        }.start();
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resendTimer != null) {
            resendTimer.cancel();
        }
    }
}