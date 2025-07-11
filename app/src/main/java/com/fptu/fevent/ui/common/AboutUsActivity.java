package com.fptu.fevent.ui.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;
import com.google.android.material.button.MaterialButton;

public class AboutUsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton btnGetInTouch, btnLearnMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about_us);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnGetInTouch = findViewById(R.id.btn_get_in_touch);
        btnLearnMore = findViewById(R.id.btn_learn_more);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnGetInTouch.setOnClickListener(v -> sendEmail());

        btnLearnMore.setOnClickListener(v -> openLearnMoreLink());
    }

    private void sendEmail() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:0398826650"));

        try {
            startActivity(callIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Không tìm thấy ứng dụng gọi điện", Toast.LENGTH_SHORT).show();
        }
    }

    private void openLearnMoreLink() {
        String url = "https://fpt.edu.vn/tin-tuc/fpt-university"; // bạn có thể đổi link này
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
