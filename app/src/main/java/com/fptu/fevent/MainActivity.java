package com.fptu.fevent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.ui.team.TeamListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Đoạn code mặc định này để xử lý viền màn hình, ta có thể tạm thời bỏ qua để tập trung vào chức năng chính.
        // Nếu nó gây ra lỗi padding không mong muốn, bạn có thể comment lại.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Tìm nút bấm "Quản lý Ban" trong layout thông qua ID của nó
        Button manageTeamsButton = findViewById(R.id.button_manage_teams);

        // 2. Thiết lập sự kiện click cho nút bấm đó
        manageTeamsButton.setOnClickListener(v -> {
            // Khi người dùng bấm vào nút, tạo một Intent để mở TeamListActivity
            Intent intent = new Intent(MainActivity.this, TeamListActivity.class);
            // Khởi chạy Activity mới
            startActivity(intent);
        });
    }
}