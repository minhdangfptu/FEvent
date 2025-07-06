package com.fptu.fevent.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.fptu.fevent.R;
import com.fptu.fevent.model.User;
import com.fptu.fevent.repository.UserRepository;
import com.fptu.fevent.ui.auth.LoginActivity;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnNext, btnSkip;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//// CHỈ thêm user nếu chưa có dữ liệu
        Executors.newSingleThreadExecutor().execute(() -> {
            UserRepository userRepository = new UserRepository(getApplication());
            List<User> existingUsers = userRepository.getAll(); // getAll là hàm sync trong repo bạn đã có

            if (existingUsers == null || existingUsers.isEmpty()) {
                User demoUser = new User();
                demoUser.name = "admin";
                demoUser.email = "admin@vn";
                demoUser.password = "1234";
                demoUser.fullname = "Quản trị viên";
                demoUser.date_of_birth = new Date();
                demoUser.phone_number = "0901234567";
                demoUser.club = "FPT Club";
                demoUser.department = "CNTT";

                userRepository.insert(demoUser);
            }
        });
//         Kiểm tra nếu người dùng đã onboarding rồi → chuyển thẳng đến Login
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        if (!prefs.getBoolean("isFirstTime", true)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);

        adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < 2) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                completeOnboarding();
            }
        });

        btnSkip.setOnClickListener(v -> completeOnboarding());
    }

    private void completeOnboarding() {
        SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
        editor.putBoolean("isFirstTime", false);
        editor.apply();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}
