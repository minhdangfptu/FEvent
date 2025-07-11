package com.fptu.fevent.ui.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;
import com.fptu.fevent.ui.auth.ChangeEmailActivity;
import com.fptu.fevent.ui.auth.ChangePasswordActivity;
import com.fptu.fevent.ui.auth.TermsOfUseActivity;
import com.fptu.fevent.ui.common.ContactUsActivity;

public class PrivacyManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_privacy_management);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int user_id = prefs.getInt("user_id", 0);
        String fullname = prefs.getString("fullname", "Tên người dùng");
        String email = prefs.getString("email", "Email chưa xác định");

        // Ánh xạ các mục trong giao diệnImageView
        ImageView btnBack = findViewById(R.id.btn_back);
        LinearLayout menuChangePassword = findViewById(R.id.menu_favourites);
        LinearLayout menuChangeEmail = findViewById(R.id.menu_downloads);
        LinearLayout menuPermission = findViewById(R.id.menu_language);
        LinearLayout menuLegal = findViewById(R.id.menu_display);
        LinearLayout menuTerms = findViewById(R.id.menu_logout);
        TextView tvUserName = findViewById(R.id.tv_user_name);
        TextView tvUserEmail = findViewById(R.id.tv_user_email);

        tvUserName.setText(fullname);
        tvUserEmail.setText(email);

        // Quay lại
        btnBack.setOnClickListener(v -> finish());

        // Thay đổi mật khẩu
        menuChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        // Thay đổi email
        menuChangeEmail.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangeEmailActivity.class));
        });

        // Quản lý quyền ứng dụng
        menuPermission.setOnClickListener(v -> {
            startActivity(new Intent(this, PermissionSettingsActivity.class));
        });

        //Liên hệ
        menuLegal.setOnClickListener(v -> {
            startActivity(new Intent(this, ContactUsActivity.class));
        });

        // Điều khoản sử dụng
        menuTerms.setOnClickListener(v -> {
            startActivity(new Intent(this, TermsOfUseActivity.class));
        });
    }
}
