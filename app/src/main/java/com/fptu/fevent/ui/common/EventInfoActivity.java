package com.fptu.fevent.ui.common;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.fevent.R;
import com.fptu.fevent.adapter.EventInfoAdapter;
import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.repository.EventInfoRepository;
import java.util.List;

public class EventInfoActivity extends AppCompatActivity {
    private EventInfoRepository eventInfoRepository;
    private RecyclerView recyclerView;
    private EventInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        eventInfoRepository = new EventInfoRepository(getApplication());
        recyclerView = findViewById(R.id.recycler_view_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            List<EventInfo> events = eventInfoRepository.getAllEventsSync();
            runOnUiThread(() -> {
                adapter = new EventInfoAdapter(events);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}