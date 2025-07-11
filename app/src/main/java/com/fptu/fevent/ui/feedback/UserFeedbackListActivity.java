package com.fptu.fevent.ui.feedback;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.fevent.R;
import com.fptu.fevent.viewmodel.UserFeedbackViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserFeedbackListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback_list);
        setTitle("Danh sách Đánh giá");

        FloatingActionButton fab = findViewById(R.id.fab_add_user_feedback);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, AddUserFeedbackActivity.class));
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view_user_feedback);
        final UserFeedbackAdapter adapter = new UserFeedbackAdapter();
        recyclerView.setAdapter(adapter);

        UserFeedbackViewModel viewModel = new ViewModelProvider(this).get(UserFeedbackViewModel.class);
        viewModel.getAllFeedbacks().observe(this, adapter::submitList);
    }
}