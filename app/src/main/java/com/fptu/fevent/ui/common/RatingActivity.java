package com.fptu.fevent.ui.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.material.button.MaterialButton;

public class RatingActivity extends AppCompatActivity {

    private ImageView[] starViews;
    private TextView tvRatingText;
    private EditText etFeedback;
    private MaterialButton btnConfirm;
    private int selectedStars = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rating);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupEvents();
    }

    private void initViews() {
        starViews = new ImageView[]{
                findViewById(R.id.star_1),
                findViewById(R.id.star_2),
                findViewById(R.id.star_3),
                findViewById(R.id.star_4),
                findViewById(R.id.star_5)
        };

        tvRatingText = findViewById(R.id.tv_rating_text);
        etFeedback = findViewById(R.id.et_feedback);
        btnConfirm = findViewById(R.id.btn_confirm);

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
    }

    private void setupEvents() {
        // Sự kiện chọn sao
        for (int i = 0; i < starViews.length; i++) {
            final int index = i;
            starViews[i].setOnClickListener(v -> {
                selectedStars = index + 1;
                updateStarUI(selectedStars);
            });
        }

        // Gửi phản hồi
        btnConfirm.setOnClickListener(v -> {
            if (selectedStars == 0) {
                Toast.makeText(this, "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
                return;
            }

            String feedback = etFeedback.getText().toString().trim();
            sendEmailFeedback(selectedStars, feedback);
        });
    }

    private void updateStarUI(int selected) {
        for (int i = 0; i < starViews.length; i++) {
            int color = i < selected ? getColor(R.color.warning_yellow) : getColor(R.color.gray_700);
            starViews[i].setColorFilter(color);
        }

        String[] ratingTexts = {"Rất tệ", "Tệ", "Tạm được", "Tốt", "Tuyệt vời"};
        tvRatingText.setText(ratingTexts[selected - 1]);
    }


    private void sendEmailFeedback(int stars, String message) {
        String subject = "Phản hồi ứng dụng FEvent - " + stars + " sao";
        String body = "Số sao đánh giá: " + stars + "\n\nNội dung phản hồi:\n" + message;

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822"); // chỉ mở ứng dụng email
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"reptitist.service@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(emailIntent, "Gửi phản hồi bằng..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Không tìm thấy ứng dụng gửi email", Toast.LENGTH_SHORT).show();
        }
    }

}
