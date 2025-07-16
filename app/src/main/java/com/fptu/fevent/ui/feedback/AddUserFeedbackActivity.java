package com.fptu.fevent.ui.feedback;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.fevent.R;
import com.fptu.fevent.database.AppDatabase;
import com.fptu.fevent.model.User;
import com.fptu.fevent.model.UserFeedback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddUserFeedbackActivity extends AppCompatActivity {
    private Spinner spinnerUsers;
    private RatingBar ratingBar;
    private EditText etComment;
    private Button btnSubmit, btnCancel;
    private AppDatabase database;
    private ExecutorService executorService;
    private List<User> users;
    private ArrayAdapter<String> userAdapter;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_feedback);

        getCurrentUserId();
        initViews();
        initDatabase();
        setupSpinner();
        setupClickListeners();
        loadUsers();
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
        spinnerUsers = findViewById(R.id.spinner_users);
        ratingBar = findViewById(R.id.rating_bar);
        etComment = findViewById(R.id.et_comment);
        btnSubmit = findViewById(R.id.btn_submit);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void initDatabase() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        users = new ArrayList<>();
    }

    private void setupSpinner() {
        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(userAdapter);
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> submitFeedback());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadUsers() {
        executorService.execute(() -> {
            try {
                List<User> userList = database.userDao().getAll();
                List<String> userNames = new ArrayList<>();
                
                // Filter out current user from the list
                for (User user : userList) {
                    if (user.id != currentUserId) {
                        userNames.add(user.fullname + " (" + user.email + ")");
                    }
                }

                runOnUiThread(() -> {
                    users.clear();
                    for (User user : userList) {
                        if (user.id != currentUserId) {
                            users.add(user);
                        }
                    }
                    userAdapter.clear();
                    userAdapter.addAll(userNames);
                    userAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tải danh sách người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void submitFeedback() {
        int userPosition = spinnerUsers.getSelectedItemPosition();
        float rating = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();

        if (userPosition < 0 || userPosition >= users.size()) {
            Toast.makeText(this, "Vui lòng chọn người dùng để đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn điểm đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            etComment.setError("Vui lòng nhập nhận xét");
            return;
        }

        User selectedUser = users.get(userPosition);
        
        UserFeedback feedback = new UserFeedback();
        feedback.from_user_id = currentUserId;
        feedback.to_user_id = selectedUser.id;
        feedback.rating = (int) rating;
        feedback.comment = comment;
        feedback.created_at = new Date();

        executorService.execute(() -> {
            try {
                database.userFeedbackDao().insert(feedback);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đánh giá đã được gửi thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi gửi đánh giá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

