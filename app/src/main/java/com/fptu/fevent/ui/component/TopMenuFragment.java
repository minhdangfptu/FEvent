package com.fptu.fevent.ui.component;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fptu.fevent.R;
import com.fptu.fevent.repository.NotificationRepository;
import com.fptu.fevent.ui.notification.NotificationActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TopMenuFragment extends Fragment {

    private ImageView btnNotification;
    private TextView notificationBadge;
    private NotificationRepository notificationRepository;
    private ExecutorService executor;
    private int currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_top_buttons, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize notification repository
        notificationRepository = new NotificationRepository(requireActivity().getApplication());
        executor = Executors.newSingleThreadExecutor();

        // Get current user
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", requireActivity().MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        // Initialize views
        ImageView btnMenu = view.findViewById(R.id.btnMenu);
        btnNotification = view.findViewById(R.id.btnNotification);
        notificationBadge = view.findViewById(R.id.notificationBadge);

        // Setup menu button
        btnMenu.setOnClickListener(v -> {
            Log.d("DEBUG_MENU", "btnMenu clicked");

            if (getActivity() == null) {
                Log.e("DEBUG_MENU", "getActivity() == null");
                return;
            }

            if (getActivity() instanceof DrawerController) {
                Log.d("DEBUG_MENU", "Activity implements DrawerController");
                ((DrawerController) getActivity()).openDrawer();
            } else {
                Log.e("DEBUG_MENU", "Activity DOES NOT implement DrawerController: "
                        + getActivity().getClass().getName());
            }
        });

        // Setup notification button
        btnNotification.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NotificationActivity.class);
            startActivity(intent);
        });

        // Load notification count
        loadNotificationCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh notification count when returning to fragment
        loadNotificationCount();
    }

    private void loadNotificationCount() {
        if (currentUserId == -1) return;

        executor.execute(() -> {
            int unreadCount = notificationRepository.getUnreadNotificationCount(currentUserId);
            
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> updateNotificationBadge(unreadCount));
            }
        });
    }

    private void updateNotificationBadge(int count) {
        if (notificationBadge != null) {
            if (count > 0) {
                notificationBadge.setVisibility(View.VISIBLE);
                notificationBadge.setText(count > 99 ? "99+" : String.valueOf(count));
            } else {
                notificationBadge.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}
