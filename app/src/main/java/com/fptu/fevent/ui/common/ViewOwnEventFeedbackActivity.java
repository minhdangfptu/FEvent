package com.fptu.fevent.ui.common;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.fptu.fevent.R;
import com.fptu.fevent.model.EventFeedback;
import com.fptu.fevent.repository.EventFeedbackRepository;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ViewOwnEventFeedbackActivity extends AppCompatActivity {
    private TextView tvRating, tvComment, tvCreatedAt;
    private EventFeedbackRepository feedbackRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_own_event_feedback);

        tvRating = findViewById(R.id.tv_feedback_rating);
        tvComment = findViewById(R.id.tv_feedback_comment);
        tvCreatedAt = findViewById(R.id.tv_feedback_created_at);

        feedbackRepository = new EventFeedbackRepository(getApplication());

        int userId = getIntent().getIntExtra("userId", -1);
        int eventId = getIntent().getIntExtra("eventId", -1);

        loadOwnFeedback(userId, eventId);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    @SuppressLint("SetTextI18n")
    private void loadOwnFeedback(int userId, int eventId) {
        new Thread(() -> {
            List<EventFeedback> feedbackList = feedbackRepository.getByUserAndEvent(userId, eventId);
            runOnUiThread(() -> {
                if (feedbackList.isEmpty()) {
                    Toast.makeText(this, "Bạn chưa gửi đánh giá nào!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    EventFeedback feedback = feedbackList.get(0);
                    tvRating.setText("Điểm: " + feedback.rating);
                    tvComment.setText("Nhận xét: " + feedback.comment);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    tvCreatedAt.setText("Ngày gửi: " + sdf.format(feedback.created_at));
                }
            });
        }).start();
    }
}