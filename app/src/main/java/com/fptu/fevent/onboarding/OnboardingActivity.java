package com.fptu.fevent.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.fptu.fevent.R;
import com.fptu.fevent.auth.LoginActivity;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnNext, btnSkip;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra nếu người dùng đã onboarding rồi → chuyển thẳng đến Login
//        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
//        if (!prefs.getBoolean("isFirstTime", true)) {
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//            return;
//        }

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
