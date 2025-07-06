package com.fptu.fevent.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.fptu.fevent.R;
import com.fptu.fevent.ui.EventInfoActivity;
import com.fptu.fevent.ui.HomeActivity;
import com.fptu.fevent.ui.DepartmentActivity;
import com.fptu.fevent.ui.ScheduleActivity;
import com.fptu.fevent.ui.TaskActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavFragment extends Fragment {

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_nav, container, false);

        BottomNavigationView nav = view.findViewById(R.id.bottomNavigationView);

        // Đặt item hiện tại nếu bạn muốn
        // nav.setSelectedItemId(R.id.nav_home);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home && !(context instanceof HomeActivity)) {
                startActivity(new Intent(context, HomeActivity.class));
            } else if (id == R.id.nav_department && !(context instanceof DepartmentActivity)) {
                startActivity(new Intent(context, DepartmentActivity.class));
            } else if (id == R.id.nav_schedule && !(context instanceof ScheduleActivity)) {
                startActivity(new Intent(context, ScheduleActivity.class));
            } else if (id == R.id.nav_task && !(context instanceof TaskActivity)) {
                startActivity(new Intent(context, TaskActivity.class));
            } else if (id == R.id.nav_info && !(context instanceof EventInfoActivity)) {
                startActivity(new Intent(context, EventInfoActivity.class));
            }
            return true;
        });

        return view;
    }
}
