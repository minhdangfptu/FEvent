package com.fptu.fevent.ui.feedback;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.adapter.UserFeedbackAdapter;
import com.fptu.fevent.database.AppDatabase;
import com.fptu.fevent.model.UserFeedback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewOwnUserFeedbackActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserFeedbackAdapter feedbackAdapter;
    private AppDatabase database;
    private ExecutorService executorService;
    private List<UserFeedback> feedbacks;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_own_user_feedback);

        getCurrentUserId();
        initViews();
        initDatabase();
        setupRecyclerView();
        loadFeedbacks();
    }

    private void getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_feedbacks);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        feedbacks = new ArrayList<>();
    }

    private void setupRecyclerView() {
        feedbackAdapter = new UserFeedbackAdapter(this, feedbacks, true); // true for own feedback view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(feedbackAdapter);
    }

    private void loadFeedbacks() {
        executorService.execute(() -> {
            try {
                List<UserFeedback> feedbackList = database.userFeedbackDao().getFeedbackForUser(currentUserId);
                runOnUiThread(() -> {
                    feedbacks.clear();
                    feedbacks.addAll(feedbackList);
                    feedbackAdapter.updateFeedbacks(feedbacks);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tải đánh giá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}

