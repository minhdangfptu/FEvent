package com.fptu.fevent.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;

public class TermsOfUseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_terms_of_use);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ các view
        ImageView backButton = findViewById(R.id.back_button);
        Button btnDecline = findViewById(R.id.btn_decline);
        Button btnAccept = findViewById(R.id.btn_accept);

        // Quay lại RegisterActivity
        backButton.setOnClickListener(v -> {
            finish(); // quay lại mà không truyền gì
        });

        // Thoát ứng dụng
        btnDecline.setOnClickListener(v -> {
            finishAffinity(); // đóng toàn bộ activity stack
        });

        // Quay lại RegisterActivity và gửi flag cbTerms = true
        btnAccept.setOnClickListener(v -> {
            Intent intent = new Intent(TermsOfUseActivity.this, RegisterActivity.class);
            intent.putExtra("termsAccepted", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
