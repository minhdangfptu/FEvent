package com.fptu.fevent.ui.common;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.fptu.fevent.R;
import com.fptu.fevent.model.EventFeedback;
import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.repository.EventFeedbackRepository;
import com.fptu.fevent.repository.EventInfoRepository;
import com.fptu.fevent.repository.UserRepository;

public class AddEventFeedbackActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    private EditText etComment;
    private EventFeedbackRepository feedbackRepository;
    private EventInfoRepository eventInfoRepository;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_feedback);

        ratingBar = findViewById(R.id.rating_bar);
        etComment = findViewById(R.id.et_comment);
        Button btnSubmit = findViewById(R.id.btn_submit);
        Button btnBack = findViewById(R.id.btn_back);

        feedbackRepository = new EventFeedbackRepository(getApplication());
        eventInfoRepository = new EventInfoRepository(getApplication());
        userRepository = new UserRepository(getApplication());


        int eventId = getIntent().getIntExtra("eventId", -1);
        int userId = getIntent().getIntExtra("userId", -1);

        btnSubmit.setOnClickListener(v -> {
            if (eventId == -1 || userId == -1) {
                Toast.makeText(this, "Dữ liệu không hợp lệ! Vui lòng kiểm tra sự kiện hoặc người dùng.", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(() -> {
                EventInfo event = eventInfoRepository.getById(eventId);
                userRepository.getUserById(userId, user -> {
                    runOnUiThread(() -> {
                        if (event == null) {
                            Toast.makeText(this, "Sự kiện không tồn tại!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (user == null) {
                            Toast.makeText(this, "Người dùng không tồn tại!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        float rating = ratingBar.getRating();
                        String comment = etComment.getText().toString().trim();
                        if (comment.isEmpty()) {
                            Toast.makeText(this, "Vui lòng nhập nhận xét!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        EventFeedback feedback = new EventFeedback();
                        feedback.eventId = eventId;
                        feedback.user_id = userId;
                        feedback.rating = (int) rating;
                        feedback.comment = comment;
                        feedback.created_at = new java.util.Date();

                        feedbackRepository.insert(feedback);
                        Toast.makeText(this, "Đã gửi đánh giá!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                });
            }).start();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}