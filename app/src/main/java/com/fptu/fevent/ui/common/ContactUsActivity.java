package com.fptu.fevent.ui.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_us);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Nút quay lại
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Gọi điện thoại
        findViewById(R.id.btn_call_us).setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:0398826650"));
            startActivity(callIntent);
        });

        // Gửi email
        findViewById(R.id.btn_email_us).setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:reptitist.service@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Liên hệ từ ứng dụng FEvent");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Xin chào đội ngũ FEvent,\n\nTôi muốn liên hệ về...");
            startActivity(Intent.createChooser(emailIntent, "Chọn ứng dụng email"));
        });

        // Mạng xã hội (giả định bạn sẽ gắn thêm URL cụ thể)
        findViewById(R.id.instagram_card).setOnClickListener(v -> openUrl("https://www.facebook.com/minhdangfptu"));
        findViewById(R.id.telegram_card).setOnClickListener(v -> openUrl("https://www.facebook.com/minhdangfptu"));
        findViewById(R.id.facebook_card).setOnClickListener(v -> openUrl("https://www.facebook.com/minhdangfptu"));
        findViewById(R.id.whatsapp_card).setOnClickListener(v -> openUrl("https://www.facebook.com/minhdangfptu"));
    }

    private void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
