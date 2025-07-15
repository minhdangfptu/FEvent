package com.fptu.fevent.ui.common;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.fptu.fevent.R;
import com.fptu.fevent.model.EventFeedback;
import com.fptu.fevent.repository.EventFeedbackRepository;
import com.fptu.fevent.repository.UserRepository;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventFeedbackDetailActivity extends AppCompatActivity {
    private TextView tvUser, tvRating, tvComment, tvCreatedAt;
    private EventFeedbackRepository feedbackRepository;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_feedback_detail);

        tvUser = findViewById(R.id.tv_feedback_user);
        tvRating = findViewById(R.id.tv_feedback_rating);
        tvComment = findViewById(R.id.tv_feedback_comment);
        tvCreatedAt = findViewById(R.id.tv_feedback_created_at);

        feedbackRepository = new EventFeedbackRepository(getApplication());
        userRepository = new UserRepository(getApplication());

        int currentUserId = getIntent().getIntExtra("currentUserId", -1);
        int feedbackId = getIntent().getIntExtra("feedbackId", -1);

        checkPrivilegedRoleAndLoad(currentUserId, feedbackId);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void checkPrivilegedRoleAndLoad(int userId, int feedbackId) {
        userRepository.getUserById(userId, user -> {
            if (user != null && (user.getRole_id() == 2 || user.getRole_id() == 3)) {
                loadFeedback(feedbackId);
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Bạn không có quyền xem chi tiết đánh giá!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadFeedback(int feedbackId) {
        new Thread(() -> {
            EventFeedback feedback = feedbackRepository.getById(feedbackId);
            if (feedback == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Không tìm thấy đánh giá!", Toast.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }

            userRepository.getUserById(feedback.user_id, user -> runOnUiThread(() -> {
                tvUser.setText("Người gửi: " + (user != null ? user.getFullName() : "Không xác định"));
                tvRating.setText("Điểm: " + feedback.rating);
                tvComment.setText("Nhận xét: " + feedback.comment);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                tvCreatedAt.setText("Ngày gửi: " + sdf.format(feedback.created_at));
            }));
        }).start();
    }
}