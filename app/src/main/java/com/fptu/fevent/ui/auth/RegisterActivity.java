package com.fptu.fevent.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.fptu.fevent.model.Team;
import com.fptu.fevent.model.User;
import com.fptu.fevent.repository.TeamRepository;
import com.fptu.fevent.repository.UserRepository;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class RegisterActivity extends AppCompatActivity {

    // view
    private EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private ImageView imgTogglePassword, imgToggleConfirm;
    private CheckBox cbTerms;
    private TextView tvError;
    private CardView errorContainer;

    private UserRepository userRepo;
    private MaterialAutoCompleteTextView actvTeam;
    private List<Team> teams;          // giữ full object để lấy id
    private ArrayAdapter<String> teamAdapter;


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
        actvTeam = findViewById(R.id.actvTeam);

        userRepo = new UserRepository(getApplication());
        Log.d("RegisterActivity", "onCreate() - before loadTeams()");
        loadTeams();
        Log.d("RegisterActivity", "onCreate() - after loadTeams()");

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
        String teamName  = actvTeam.getText().toString().trim();

        // ✅ Lấy teamId theo tên
        final int[] matchedTeamId = { -1 };
        for (Team t : teams) {
            if (t.name.equals(teamName)) {
                matchedTeamId[0] = t.id;
                break;
            }
        }


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
        if (matchedTeamId[0] == -1) {
            showError("Vui lòng chọn Ban tham gia");
            return;
        }

        /* 2. Kiểm tra email trùng */
        userRepo.isEmailExists(email, exists -> runOnUiThread(() -> {
            if (exists) {
                showError("Email đã được sử dụng");
            } else {
                /* 3. Insert user */
                User newUser = new User(name, email, pass);
                newUser.team_id = matchedTeamId[0];
                newUser.role_id = 4;

                Log.d("RegisterActivity", "Inserting user: " + newUser.name);
                userRepo.insertAsync(newUser, id -> runOnUiThread(() -> {
                    Log.d("RegisterActivity", "Insert returned ID: " + id);
                    if (id > 0) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
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

    private void loadTeams() {
        TeamRepository teamRepo = new TeamRepository(getApplication());
        teamRepo.getAllAsync(teams -> runOnUiThread(() -> {
            this.teams = teams;
            Log.d("RegisterActivity", "loadTeams() called");

            if (teams == null || teams.isEmpty()) {
                Toast.makeText(this, "Không có dữ liệu Team", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> teamNames = new ArrayList<>();
            for (Team t : teams) {
                teamNames.add(t.name);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    teamNames
            );
            actvTeam.setAdapter(adapter);
        }));
    }


}

