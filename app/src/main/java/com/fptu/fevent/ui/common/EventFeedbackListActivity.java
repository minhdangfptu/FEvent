package com.fptu.fevent.ui.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.fevent.R;
import com.fptu.fevent.adapter.FeedbackAdapter;
import com.fptu.fevent.model.EventFeedback;
import com.fptu.fevent.repository.EventFeedbackRepository;
import com.fptu.fevent.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

public class EventFeedbackListActivity extends AppCompatActivity {
    private FeedbackAdapter adapter;
    private final List<EventFeedback> feedbackList = new ArrayList<>();
    private EventFeedbackRepository feedbackRepository;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_feedback_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_feedback);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbackRepository = new EventFeedbackRepository(getApplication());
        userRepository = new UserRepository(getApplication());

        adapter = new FeedbackAdapter(feedbackList, feedback -> {
            // Open detail activity for feedback
            Intent intent = new Intent(this, ViewOwnEventFeedbackActivity.class);
            intent.putExtra("feedbackId", feedback.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        int userId = getIntent().getIntExtra("userId", -1);
        int eventId = getIntent().getIntExtra("eventId", -1);
        checkPrivilegedRoleAndLoad(userId, eventId);


        loadFeedback(eventId);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void checkPrivilegedRoleAndLoad(int userId, int eventId) {
        userRepository.getUserById(userId, user -> {
            if (user != null && (user.getRole_id() == 2 || user.getRole_id() == 3)) {
                loadFeedback(eventId);
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Bạn không có quyền xem danh sách đánh giá!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadFeedback(int eventId) {
        new Thread(() -> {
            feedbackList.clear();
            feedbackList.addAll(feedbackRepository.getByEventId(eventId));
            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                if (feedbackList.isEmpty()) {
                    Toast.makeText(this, "Không có đánh giá nào cho sự kiện này.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}