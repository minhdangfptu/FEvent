package com.fptu.fevent.ui.component;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fptu.fevent.MainActivity;
import com.fptu.fevent.R;

public class TopMenuFragment extends Fragment {

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

        ImageView btnMenu = view.findViewById(R.id.btnMenu);
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
    }
}
