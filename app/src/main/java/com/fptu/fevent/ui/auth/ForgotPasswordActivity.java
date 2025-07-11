package com.fptu.fevent.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    CardView emailOption, twofaOption, googleAuthOption;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Khởi tạo view
        emailOption = findViewById(R.id.email_option);
        twofaOption = findViewById(R.id.twofa_option);
        googleAuthOption = findViewById(R.id.google_auth_option);
        backButton = findViewById(R.id.back_button);

        // Sự kiện quay lại
        backButton.setOnClickListener(v -> onBackPressed());

        // Sự kiện chọn "Địa chỉ Email"
        emailOption.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, ForgotPasswordEmailActivity.class);
            startActivity(intent);
        });

        // Các chức năng chưa hỗ trợ
        twofaOption.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng sắp ra mắt", Toast.LENGTH_SHORT).show();
        });

        googleAuthOption.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng sắp ra mắt", Toast.LENGTH_SHORT).show();
        });

        // Gửi yêu cầu reset (tuỳ mục đích có thể disable hoặc show toast)
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

}