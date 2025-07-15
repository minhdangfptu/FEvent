//package com.fptu.fevent.ui.common;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.fptu.fevent.R;
//import com.fptu.fevent.adapter.EventInfoAdapter;
//import com.fptu.fevent.model.EventInfo;
//import com.fptu.fevent.repository.EventInfoRepository;
//
//import java.util.List;
//
//public class EventInfoFragment extends Fragment {
//
//    private EventInfoRepository eventInfoRepository;
//    private RecyclerView recyclerView;
//    private EventInfoAdapter adapter;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_event_info, container, false);
//
//        // Initialize repository
//        Activity activity = getActivity();
//        if (activity != null) {
//            eventInfoRepository = new EventInfoRepository(activity.getApplication());
//        } else {
//            return view; // Thoát sớm nếu không có Activity
//        }
//
//        recyclerView = view.findViewById(R.id.recycler_view_events);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        new Thread(() -> {
//            List<EventInfo> events = eventInfoRepository.getAllEventsSync();
//            if (getActivity() != null) {
//                getActivity().runOnUiThread(() -> {
//                    adapter = new EventInfoAdapter(events);
//                    recyclerView.setAdapter(adapter);
//                });
//            }
//        }).start();
//
//        return view;
//    }
//}