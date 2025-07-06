package com.fptu.fevent.ui.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
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
import com.fptu.fevent.repository.UserRepository;
import com.fptu.fevent.ui.HomeActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private TextView tvClock;
    private TextView btnLogin;
    private EditText edtUsername, edtPassword;
    private ImageView imgTogglePassword;       // ← icon con mắt
    private boolean isPasswordVisible = false; // trạng thái hiển thị mật khẩu
    private final Handler handler = new Handler();
    private UserRepository userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Áp dụng padding hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        tvClock          = findViewById(R.id.tvClock);
        edtUsername      = findViewById(R.id.edtUsername);
        edtPassword      = findViewById(R.id.edtPassword);
        btnLogin         = findViewById(R.id.btnLogin);
        imgTogglePassword = findViewById(R.id.imgTogglePassword);

        userRepo = new UserRepository(getApplication());

        /* ===== XỬ LÝ ẨN/HIỆN MẬT KHẨU ===== */
        imgTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Chuyển sang ẩn mật khẩu
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.baseline_remove_red_eye_24); // mắt mở
            } else {
                // Chuyển sang hiện mật khẩu
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.baseline_visibility_off_24); // mắt gạch
            }
            // Giữ con trỏ ở cuối
            edtPassword.setSelection(edtPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        startClockUpdater(); // Bắt đầu cập nhật thời gian

        /* ===== XỬ LÝ ĐĂNG NHẬP ===== */
        btnLogin.setOnClickListener(v -> {
            String email    = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            userRepo.login(email, password, user -> runOnUiThread(() -> {
                if (user != null) {
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("user_id", user.id);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu!", Toast.LENGTH_LONG).show();
                }
            }));
        });
    }

    /* ===== ĐỒNG HỒ REAL-TIME ===== */
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
}
