package com.fptu.fevent.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;

public class ForgotVerifyActivity extends AppCompatActivity {

    private EditText[] otpInputs = new EditText[6];
    private Button btnVerify;
    private TextView tvResend;
    private ImageView backButton;

    private String sentOtp; // OTP được truyền qua intent
    private String email;   // Email người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_verify);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        getExtras();
        setupListeners();
    }

    private void initViews() {
        otpInputs[0] = findViewById(R.id.otp_1);
        otpInputs[1] = findViewById(R.id.otp_2);
        otpInputs[2] = findViewById(R.id.otp_3);
        otpInputs[3] = findViewById(R.id.otp_4);
        otpInputs[4] = findViewById(R.id.otp_5);
        otpInputs[5] = findViewById(R.id.otp_6);

        btnVerify = findViewById(R.id.btn_verify_code);
        tvResend = findViewById(R.id.tv_resend_code);
        backButton = findViewById(R.id.back_button);
    }

    private void getExtras() {
        Intent intent = getIntent();
        sentOtp = intent.getStringExtra("otp");
        email = intent.getStringExtra("email");
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> onBackPressed());

        btnVerify.setOnClickListener(v -> {
            StringBuilder enteredOtp = new StringBuilder();
            for (EditText edt : otpInputs) {
                String digit = edt.getText().toString().trim();
                if (digit.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ mã xác thực", Toast.LENGTH_SHORT).show();
                    return;
                }
                enteredOtp.append(digit);
            }

            if (enteredOtp.toString().equals(sentOtp)) {
                Toast.makeText(this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();

                // 👉 Chuyển sang màn hình đổi mật khẩu
                Intent intent = new Intent(this, ResetPasswordActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Mã xác thực không chính xác", Toast.LENGTH_SHORT).show();
            }
        });

        tvResend.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng gửi lại mã đang phát triển", Toast.LENGTH_SHORT).show()
        );
    }
}
