package com.fptu.fevent.ui.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
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
import com.fptu.fevent.model.User;
import com.fptu.fevent.repository.UserRepository;
import com.fptu.fevent.ui.common.CommonUIActivity;
import com.fptu.fevent.ui.common.HomeActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private TextView tvClock;
    private TextView btnLogin;
    private EditText edtUsername, edtPassword;
    private ImageView imgTogglePassword;
    private boolean isPasswordVisible = false;
    private final Handler handler = new Handler();
    private UserRepository userRepo;

    private TextView tvForgot;
    private TextView tvSignup; // Nếu bạn muốn thêm điều hướng tới Sign Up riêng


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ View
        tvClock           = findViewById(R.id.tvClock);
        edtUsername       = findViewById(R.id.edtUsername);
        edtPassword       = findViewById(R.id.edtPassword);
        btnLogin          = findViewById(R.id.btnLogin);
        imgTogglePassword = findViewById(R.id.imgTogglePassword);
        tvForgot          = findViewById(R.id.tvForgot);
        tvSignup          = findViewById(R.id.tvSignup);

        userRepo = new UserRepository(getApplication());

        imgTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.baseline_remove_red_eye_24);
            } else {
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.baseline_visibility_off_24);
            }
            edtPassword.setSelection(edtPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        startClockUpdater();
        navigateToSignup();
        handleLogin();
    }
    private void handleLogin() {
        btnLogin.setOnClickListener(v -> {
            String email = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            userRepo.login(email, password, user -> runOnUiThread(() -> {
                if (user != null) {
                    saveUserToPrefs(user);
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, CommonUIActivity.class);
                    startActivity(intent);
                    finish(); // đóng trang login
                } else {
                    Toast.makeText(this, "Sai thông tin đăng nhập hoặc tài khoản bị khóa", Toast.LENGTH_LONG).show();
                }
            }));
        });
    }

    private void startClockUpdater() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Locale vietnamese = new Locale("vi", "VN");
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss '\n' EEEE, d/M/yyyy", vietnamese);
                tvClock.setText(sdf.format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 0);
    }

    private void navigateToSignup() {
        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        // Nếu có nút tvSignup
        if (tvSignup != null) {
            tvSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
    private void saveUserToPrefs(User user) {
        getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putInt("user_id", user.id)
                .putString("email", user.email)
                .putString("fullname", user.fullname)
                .putInt("role_id", user.role_id != null ? user.role_id : -1)
                .putInt("team_id", user.team_id != null ? user.team_id : -1)
                .putString("position", user.position != null ? user.position : "")
                .apply();
    }
}
