package com.fptu.fevent.ui.common;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.fevent.R;
import com.fptu.fevent.adapter.EventInfoAdapter;
import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.repository.EventInfoRepository;
import java.util.List;

public class EventInfoActivity extends AppCompatActivity implements EventInfoAdapter.OnItemClickListener, EventInfoAdapter.OnFeedbackClickListener {
    private EventInfoRepository eventInfoRepository;
    private RecyclerView recyclerView;
    private EventInfoAdapter adapter;

    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        currentUserId = getCurrentUserId();

        eventInfoRepository = new EventInfoRepository(getApplication());
        recyclerView = findViewById(R.id.recycler_view_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            List<EventInfo> events = eventInfoRepository.getAllEventsSync();
            runOnUiThread(() -> {
                adapter = new EventInfoAdapter(events, this, this);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
    @Override
    public void onItemClick(EventInfo event) {
        Intent intent = new Intent(this, EventInfoDetailActivity.class);
        intent.putExtra("eventId", event.id);
        startActivity(intent);
    }

    @Override
    public void onFeedbackClick(EventInfo event) {
        Intent intent = new Intent(this, AddEventFeedbackActivity.class);
        intent.putExtra("eventId", event.id);
        intent.putExtra("userId", currentUserId);
        startActivity(intent);
    }

    private int getCurrentUserId() {
        return 2;
    }
}