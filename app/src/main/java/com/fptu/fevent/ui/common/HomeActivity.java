package com.fptu.fevent.ui.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fptu.fevent.R;
import com.fptu.fevent.ui.auth.LoginActivity;
import com.fptu.fevent.ui.component.DrawerController;
import com.fptu.fevent.ui.component.TopMenuFragment;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements DrawerController {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home); // Đảm bảo activity_home.xml có DrawerLayout với id = drawer_layout

        NavigationView navView = findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);

// Lấy thông tin từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String fullname = prefs.getString("fullname", "Tên người dùng");
        String email = prefs.getString("email", "Email chưa xác định");
        String position = prefs.getString("position", "Position chưa xác định");

// Ánh xạ View trong layout nav_header.xml
        TextView tvFullname = headerView.findViewById(R.id.tvUsername);
        TextView tvEmail = headerView.findViewById(R.id.tvUserEmail);
        TextView tvPosition = headerView.findViewById(R.id.tvUserPosition);
        TextView tvUserName = findViewById(R.id.tvUserName);


// Gán dữ liệu
        tvFullname.setText(fullname);
        tvEmail.setText(email);
        tvPosition.setText(position);
        tvUserName.setText("Hi " + fullname);
        // Gán DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);

        // Cài đặt Edge-to-Edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Gắn TopMenuFragment nếu chưa có
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_menu_fragment_container, new TopMenuFragment())
                    .commit();
        }
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                performLogout();
                return true;
            }
            return false;
        });
    }

    @Override
    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
            Log.d("DEBUG_MENU", "Drawer opened from HomeActivity");
        } else {
            Log.e("DEBUG_MENU", "drawerLayout is null in HomeActivity");
        }
    }

    private void performLogout() {
        // Xoá session, token, v.v. nếu có
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        preferences.edit().clear().apply();

        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
