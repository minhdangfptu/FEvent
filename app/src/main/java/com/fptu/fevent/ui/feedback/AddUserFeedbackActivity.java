package com.fptu.fevent.ui.feedback;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.fptu.fevent.R;
import com.fptu.fevent.model.User;
import com.fptu.fevent.model.UserFeedback;
import com.fptu.fevent.viewmodel.UserFeedbackViewModel;
import com.fptu.fevent.viewmodel.UserViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Date;

public class AddUserFeedbackActivity extends AppCompatActivity {

    private Spinner spinnerSelectUser;
    private RatingBar ratingBar;
    private TextInputEditText editTextComment;
    private UserFeedbackViewModel userFeedbackViewModel;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_FEvent_NoActionBar);
        setContentView(R.layout.activity_add_user_feedback);

        Toolbar toolbar = findViewById(R.id.toolbar_add_user_feedback);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Thêm đánh giá mới");
        }

        spinnerSelectUser = findViewById(R.id.spinner_select_user);
        ratingBar = findViewById(R.id.rating_bar_add_feedback);
        editTextComment = findViewById(R.id.edit_text_feedback_comment);

        userFeedbackViewModel = new ViewModelProvider(this).get(UserFeedbackViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        ArrayAdapter<User> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectUser.setAdapter(adapter);

        userViewModel.getAllUsers().observe(this, users -> {
            adapter.clear();
            adapter.addAll(users);
            adapter.notifyDataSetChanged();
        });
    }

    private void saveFeedback() {
        User toUser = (User) spinnerSelectUser.getSelectedItem();
        float rating = ratingBar.getRating();
        String comment = editTextComment.getText().toString().trim();

        if (toUser == null) {
            Toast.makeText(this, "Vui lòng chọn thành viên", Toast.LENGTH_SHORT).show();
            return;
        }
        if (comment.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nhận xét", Toast.LENGTH_SHORT).show();
            return;
        }

        UserFeedback feedback = new UserFeedback();
        feedback.from_user_id = 1; // Tạm thời hardcode người đánh giá là User 1 (Admin/TBTC)
        feedback.to_user_id = toUser.id;
        feedback.rating = (int) rating;
        feedback.comment = comment;
        feedback.created_at = new Date();

        userFeedbackViewModel.insert(feedback);
        Toast.makeText(this, "Đã lưu đánh giá", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveFeedback();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}