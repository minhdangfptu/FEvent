package com.fptu.fevent.ui.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fptu.fevent.R;
import com.fptu.fevent.ui.admin.AdminUserManagementActivity;
import com.fptu.fevent.ui.auth.LoginActivity;
import com.fptu.fevent.ui.component.DrawerController;
import com.fptu.fevent.ui.component.TopMenuFragment;
import com.fptu.fevent.ui.user.PrivacyManagementActivity;
import com.fptu.fevent.ui.user.UserManagementActivity;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements DrawerController {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        // Lấy thông tin từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String fullname = prefs.getString("fullname", "Tên người dùng");
        String email = prefs.getString("email", "Email chưa xác định");
        String position = prefs.getString("position", "Position chưa xác định");
        int roleId = prefs.getInt("role_id", 0); // 1 = admin

        // Start notification background service
        Intent serviceIntent = new Intent(this, com.fptu.fevent.service.NotificationBackgroundService.class);
        startService(serviceIntent);

        // Create sample notifications for testing (only on first run)
        if (prefs.getBoolean("first_run", true)) {
            com.fptu.fevent.util.NotificationHelper notificationHelper = new com.fptu.fevent.util.NotificationHelper(getApplication());
            notificationHelper.createSampleNotifications();
            prefs.edit().putBoolean("first_run", false).apply();
        }

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

        // Ánh xạ View trong layout nav_header.xml và activity_home.xml
        TextView tvFullname = headerView.findViewById(R.id.tvUsername);
        TextView tvEmail = headerView.findViewById(R.id.tvUserEmail);
        TextView tvPosition = headerView.findViewById(R.id.tvUserPosition);
        TextView tvUserName = findViewById(R.id.tvUserName);

        // Gán dữ liệu
        tvFullname.setText(fullname);
        tvEmail.setText(email);
        tvPosition.setText(position);
        if (tvUserName != null) {
            tvUserName.setText("Hi " + fullname);
        }

        // Áp dụng padding hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Gắn TopMenuFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_menu_fragment_container, new TopMenuFragment())
                    .commit();
        }

        // Xử lý menu navigation
        navigationView.setNavigationItemSelectedListener(item -> {
            handleDrawerItemClick(item);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
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

    private void handleDrawerItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_notification) {
            Intent intent = new Intent(HomeActivity.this, com.fptu.fevent.ui.notification.NotificationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
//            Toast.makeText(this, "Quản lý tài khoản", Toast.LENGTH_SHORT).show();
            manageAccount();
        } else if (id == R.id.nav_secure) {
//            Toast.makeText(this, "Bảo mật tài khoản", Toast.LENGTH_SHORT).show();
            navigateToPrivacy();
        } else if (id == R.id.nav_settings){
//            Toast.makeText(this, "Cài đặt ứng dụng", Toast.LENGTH_SHORT).show();
            openAppInStore();
        } else if (id == R.id.nav_linked) {
//            Toast.makeText(this, "Ứng dụng liên kết", Toast.LENGTH_SHORT).show();
            openMyFapApp();
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_rate) {
//            Toast.makeText(this, "Xếp hạng ứng dụng", Toast.LENGTH_SHORT).show();
            navigateToRating();
        } else if (id == R.id.nav_about) {
//            Toast.makeText(this, "Về chúng tôi", Toast.LENGTH_SHORT).show();
            navigateToAboutUs();
        } else if (id == R.id.nav_support) {
//            Toast.makeText(this, "Liên hệ hỗ trợ", Toast.LENGTH_SHORT).show();
            navigateToContactUs();
        } else if (id == R.id.nav_manage_users) {
//            Toast.makeText(this, "Liên hệ hỗ trợ", Toast.LENGTH_SHORT).show();
            navigateToManageUser();
        } else if (id == R.id.nav_logout) {
            performLogout();
        } else {
            Toast.makeText(this, "Chức năng chưa hỗ trợ", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void navigateToManageUser() {
        Intent intent = new Intent(HomeActivity.this, AdminUserManagementActivity.class);
        startActivity(intent);
    }


    private void performLogout() {
        // Xoá session
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        preferences.edit().clear().apply();

        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

        // Chuyển về login
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "FEvent App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hãy thử ngay ứng dụng FEvent: https://fptu.fevent.vn");
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
    }
    private void manageAccount() {
        Intent intent = new Intent(HomeActivity.this, UserManagementActivity.class);
        startActivity(intent);
    }
    private void navigateToPrivacy() {
        Intent intent = new Intent(HomeActivity.this, PrivacyManagementActivity.class);
        startActivity(intent);
    }
    private void navigateToRating() {
        Intent intent = new Intent(HomeActivity.this, RatingActivity.class);
        startActivity(intent);
    }
    private void navigateToAboutUs() {
        Intent intent = new Intent(HomeActivity.this, AboutUsActivity.class);
        startActivity(intent);
    }
    private void navigateToContactUs() {
        Intent intent = new Intent(HomeActivity.this, ContactUsActivity.class);
        startActivity(intent);
    }
    private void openAppInStore() {
        final String appPackageName = getPackageName(); // Lấy package hiện tại

        try {
            // Mở bằng Google Play App
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            // Nếu không có Play Store thì mở bằng trình duyệt
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
    private void openMyFapApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.fuct");
        if (launchIntent != null) {
            startActivity(launchIntent); // Mở app nếu đã cài
        } else {
            // Nếu chưa cài, mở trang ứng dụng trên CH Play
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.fuct")));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.fuct")));
            }
        }
    }



}
