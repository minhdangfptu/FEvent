package com.fptu.fevent.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;
import com.fptu.fevent.model.User;
import com.fptu.fevent.repository.UserRepository;

public class RegisterActivity extends AppCompatActivity {

    // view
    private EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private ImageView imgTogglePassword, imgToggleConfirm;
    private CheckBox cbTerms;
    private TextView tvError;
    private CardView errorContainer;

    private UserRepository userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        /* ========== insets ========== */
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        /* ========== ÁNH XẠ ========== */
        edtName            = findViewById(R.id.edtName);
        edtEmail           = findViewById(R.id.edtEmail);
        edtPassword        = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        imgTogglePassword  = findViewById(R.id.imgTogglePassword);
        imgToggleConfirm   = findViewById(R.id.imgToggleConfirmPassword);
        cbTerms            = findViewById(R.id.cbTerms);
        tvError            = findViewById(R.id.tvError);
        errorContainer     = findViewById(R.id.errorContainer);
        TextView tvTerms   = findViewById(R.id.tvTerms);
        TextView tvSignIn  = findViewById(R.id.tvSignIn);
        TextView btnRegister= findViewById(R.id.btnRegister);

        userRepo = new UserRepository(getApplication());

        /* ========== TỰ CHECK BOX NẾU ĐÃ ACCEPT ========== */
        if (getIntent().getBooleanExtra("termsAccepted", false)) cbTerms.setChecked(true);

        /* ========== MỞ TRANG ĐIỀU KHOẢN ========== */
        tvTerms.setOnClickListener(v ->
                startActivity(new Intent(this, TermsOfUseActivity.class)));

        /* ========== QUA TRANG LOGIN ========== */
        tvSignIn.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        /* ========== SHOW / HIDE PASSWORD ========== */
        imgTogglePassword.setOnClickListener(v -> togglePassword(edtPassword, imgTogglePassword));
        imgToggleConfirm .setOnClickListener(v -> togglePassword(edtConfirmPassword, imgToggleConfirm));

        /* ========== XỬ LÝ ĐĂNG KÝ ========== */
        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    /* ---------- VALIDATE & INSERT ---------- */
    private void attemptRegister() {
        String name      = edtName.getText().toString().trim();
        String email     = edtEmail.getText().toString().trim();
        String pass      = edtPassword.getText().toString();
        String passAgain = edtConfirmPassword.getText().toString();

        /* 1. Kiểm tra dữ liệu */
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || passAgain.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Địa chỉ email không hợp lệ");
            return;
        }
        if (!pass.equals(passAgain)) {
            showError("Mật khẩu không khớp");
            return;
        }
        if (pass.length() < 6) {
            showError("Mật khẩu phải từ 6 ký tự");
            return;
        }
        if (!cbTerms.isChecked()) {
            showError("Bạn phải đồng ý Điều khoản sử dụng");
            return;
        }

        /* 2. Kiểm tra email trùng */
        userRepo.isEmailExists(email, exists -> runOnUiThread(() -> {
            if (exists) {
                showError("Email đã được sử dụng");
            } else {
                /* 3. Insert user */
                User newUser = new User(name, email, pass);
                userRepo.insertAsync(newUser, id -> runOnUiThread(() -> {
                    if (id > 0) {
                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    } else {
                        showError("Đăng ký thất bại – thử lại!");
                    }
                }));
            }
        }));
    }

    /* ---------- Helpers ---------- */

    private boolean passwordVisible = false;
    private void togglePassword(EditText edt, ImageView img) {
        passwordVisible = !passwordVisible;
        int type = passwordVisible
                ? (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                : (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edt.setInputType(type);
        edt.setSelection(edt.length());
        img.setImageResource(passwordVisible
                ? R.drawable.baseline_visibility_off_24
                : R.drawable.baseline_remove_red_eye_24);
    }

    private void showError(String msg) {
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(msg);
        errorContainer.setVisibility(View.VISIBLE);
    }
}

