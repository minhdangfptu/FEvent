package com.fptu.fevent.ui.common;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.fptu.fevent.R;
import com.fptu.fevent.ui.component.DrawerController;
import com.google.android.material.navigation.NavigationView;

public class CommonUIActivity extends AppCompatActivity implements DrawerController {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_ui);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Xử lý menu navigation
        navigationView.setNavigationItemSelectedListener(item -> {
            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof HomeFragment) {
                ((HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container)).handleDrawerItemClick(item.getItemId());
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Xử lý menu dựa trên role_id
        int roleId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("role_id", 0);
        if (roleId == 1) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_settings).setVisible(false);
            menu.findItem(R.id.nav_linked).setVisible(false);
            menu.findItem(R.id.nav_share).setVisible(false);
            menu.findItem(R.id.nav_rate).setVisible(false);
            menu.findItem(R.id.nav_about).setVisible(false);
            menu.findItem(R.id.nav_support).setVisible(false);
            menu.findItem(R.id.nav_manage_users).setVisible(true);
            menu.findItem(R.id.nav_manage_event).setVisible(true);
        }

        // Ánh xạ và gán dữ liệu cho NavigationView header
        View headerView = navigationView.getHeaderView(0);
        TextView tvFullname = headerView.findViewById(R.id.tvUsername);
        TextView tvEmail = headerView.findViewById(R.id.tvUserEmail);
        TextView tvPosition = headerView.findViewById(R.id.tvUserPosition);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String fullname = prefs.getString("fullname", "Tên người dùng");
        String email = prefs.getString("email", "Email chưa xác định");
        String position = prefs.getString("position", "Position chưa xác định");

        tvFullname.setText(fullname);
        tvEmail.setText(email);
        tvPosition.setText(position);

        // Khởi tạo BottomNavigationView
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        // Handle navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_department) {
                selectedFragment = new DepartmentFragment();
            } else if (itemId == R.id.nav_schedule) {
                selectedFragment = new ScheduleFragment();
            } else if (itemId == R.id.nav_task) {
                selectedFragment = new TaskFragment();
//            } else if (itemId == R.id.nav_info) {
//                selectedFragment = new FeedbackFragment();
            }


            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    @Override
    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}