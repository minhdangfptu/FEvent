package com.fptu.fevent.ui.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.fptu.fevent.R;
import com.fptu.fevent.ui.admin.AdminUserManagementActivity;
import com.fptu.fevent.ui.auth.LoginActivity;
import com.fptu.fevent.ui.component.DrawerController;
import com.fptu.fevent.ui.component.TopMenuFragment;
import com.fptu.fevent.ui.user.PrivacyManagementActivity;
import com.fptu.fevent.ui.user.UserManagementActivity;

public class HomeFragment extends Fragment implements DrawerController {

    private TextView tvUserName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Xử lý WindowInsets để tránh bị đè lên status bar
        View mainLayout = view.findViewById(R.id.main_container);  // main là ID của ViewGroup gốc trong layout
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Ánh xạ View
        tvUserName = view.findViewById(R.id.tvUserName);

        // Lấy thông tin từ SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", requireActivity().MODE_PRIVATE);
        String fullname = prefs.getString("fullname", "Tên người dùng");

        // Gán dữ liệu
        if (tvUserName != null) {
            tvUserName.setText("Hi " + fullname);
        }

        // Gắn TopMenuFragment
        if (savedInstanceState == null) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_menu_fragment_container, new TopMenuFragment())
                    .commit();
        }

        // Start notification background service
        Intent serviceIntent = new Intent(requireContext(), com.fptu.fevent.service.NotificationBackgroundService.class);
        requireActivity().startService(serviceIntent);

        // Create sample notifications for testing (only on first run)
        if (prefs.getBoolean("first_run", true)) {
            com.fptu.fevent.util.NotificationHelper notificationHelper =
                    new com.fptu.fevent.util.NotificationHelper(requireActivity().getApplication());
            notificationHelper.createSampleNotifications();
            prefs.edit().putBoolean("first_run", false).apply();
        }

        return view;
    }

    @Override
    public void openDrawer() {
        if (requireActivity() instanceof DrawerController) {
            ((DrawerController) requireActivity()).openDrawer();
        }
    }

    // Các phương thức điều hướng từ NavigationView
    public void handleDrawerItemClick(int id) {
        if (id == R.id.nav_notification) {
            Intent intent = new Intent(requireContext(), com.fptu.fevent.ui.notification.NotificationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            manageAccount();
        } else if (id == R.id.nav_secure) {
            navigateToPrivacy();
        } else if (id == R.id.nav_settings) {
            openAppInStore();
        } else if (id == R.id.nav_linked) {
            openMyFapApp();
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_rate) {
            navigateToRating();
        } else if (id == R.id.nav_about) {
            navigateToAboutUs();
        } else if (id == R.id.nav_support) {
            navigateToContactUs();
        } else if (id == R.id.nav_manage_users) {
            navigateToManageUser();
        } else if (id == R.id.nav_logout) {
            performLogout();
        } else {
            Toast.makeText(requireContext(), "Chức năng chưa hỗ trợ", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToManageUser() {
        Intent intent = new Intent(requireContext(), AdminUserManagementActivity.class);
        startActivity(intent);
    }

    private void performLogout() {
        // Xoá session
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", requireActivity().MODE_PRIVATE);
        preferences.edit().clear().apply();

        Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

        // Chuyển về login
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "FEvent App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hãy thử ngay ứng dụng FEvent: https://fptu.fevent.vn");
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
    }

    private void manageAccount() {
        Intent intent = new Intent(requireContext(), UserManagementActivity.class);
        startActivity(intent);
    }

    private void navigateToPrivacy() {
        Intent intent = new Intent(requireContext(), PrivacyManagementActivity.class);
        startActivity(intent);
    }

    private void navigateToRating() {
        Intent intent = new Intent(requireContext(), RatingActivity.class);
        startActivity(intent);
    }

    private void navigateToAboutUs() {
        Intent intent = new Intent(requireContext(), AboutUsActivity.class);
        startActivity(intent);
    }

    private void navigateToContactUs() {
        Intent intent = new Intent(requireContext(), ContactUsActivity.class);
        startActivity(intent);
    }

    private void openAppInStore() {
        final String appPackageName = requireContext().getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void openMyFapApp() {
        Intent launchIntent = requireContext().getPackageManager().getLaunchIntentForPackage("com.fuct");
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
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