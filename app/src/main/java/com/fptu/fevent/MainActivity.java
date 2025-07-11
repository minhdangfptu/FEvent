package com.fptu.fevent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// ĐẢM BẢO CÓ CẢ 2 DÒNG IMPORT NÀY
import com.fptu.fevent.ui.event.EventListActivity;
import com.fptu.fevent.ui.team.TeamListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Nút Quản lý Ban
        Button manageTeamsButton = findViewById(R.id.button_manage_teams);
        manageTeamsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TeamListActivity.class);
            startActivity(intent);
        });

        // Nút Quản lý Sự kiện
        Button manageEventsButton = findViewById(R.id.button_manage_events);
        manageEventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EventListActivity.class);
            startActivity(intent);
        });

        Button manageFeedbacksButton = findViewById(R.id.button_manage_feedbacks);
        manageFeedbacksButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.fptu.fevent.ui.feedback.UserFeedbackListActivity.class);
            startActivity(intent);
        });
    }
}