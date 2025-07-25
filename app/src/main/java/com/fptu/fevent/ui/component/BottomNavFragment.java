package com.fptu.fevent.ui.component;

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
import com.fptu.fevent.ui.common.AddEventFeedbackActivity;
import com.fptu.fevent.ui.common.EventTimelineActivity;
import com.fptu.fevent.ui.common.HomeActivity;
import com.fptu.fevent.ui.common.ScheduleActivity;
import com.fptu.fevent.ui.common.TaskActivity;
import com.fptu.fevent.ui.team.TeamListActivity;
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

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home && !(context instanceof HomeActivity)) {
                requireActivity().startActivity(new Intent(context, HomeActivity.class));
            } else if (id == R.id.nav_department && !(context instanceof TeamListActivity)) {
                requireActivity().startActivity(new Intent(context, TeamListActivity.class));
            } else if (id == R.id.nav_schedule && !(context instanceof ScheduleActivity) && !(context instanceof EventTimelineActivity)) {
                requireActivity().startActivity(new Intent(context, EventTimelineActivity.class));
            } else if (id == R.id.nav_task && !(context instanceof TaskActivity)) {
                requireActivity().startActivity(new Intent(context, TaskActivity.class));
            } else if (id == R.id.nav_info && !(context instanceof AddEventFeedbackActivity)) {
                requireActivity().startActivity(new Intent(context, AddEventFeedbackActivity.class));
            }
            return true;
        });

        return view;
    }
}
