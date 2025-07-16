package com.fptu.fevent.ui.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.adapter.UserFeedbackAdapter;
import com.fptu.fevent.database.AppDatabase;
import com.fptu.fevent.model.UserFeedback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserFeedbackListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserFeedbackAdapter feedbackAdapter;
    private FloatingActionButton fabAddFeedback;
    private AppDatabase database;
    private ExecutorService executorService;
    private List<UserFeedback> feedbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback_list);

        initViews();
        initDatabase();
        setupRecyclerView();
        setupClickListeners();
        loadFeedbacks();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_feedbacks);
        fabAddFeedback = findViewById(R.id.fab_add_feedback);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        feedbacks = new ArrayList<>();
    }

    private void setupRecyclerView() {
        feedbackAdapter = new UserFeedbackAdapter(this, feedbacks, false); // false for general feedback list
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(feedbackAdapter);
    }

    private void setupClickListeners() {
        fabAddFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddUserFeedbackActivity.class);
            startActivity(intent);
        });
    }

    private void loadFeedbacks() {
        executorService.execute(() -> {
            try {
                List<UserFeedback> feedbackList = database.userFeedbackDao().getAll();
                runOnUiThread(() -> {
                    feedbacks.clear();
                    feedbacks.addAll(feedbackList);
                    feedbackAdapter.updateFeedbacks(feedbacks);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tải danh sách đánh giá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFeedbacks(); // Refresh data when returning to this activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}

