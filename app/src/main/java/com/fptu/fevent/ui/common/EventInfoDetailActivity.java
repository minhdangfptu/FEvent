package com.fptu.fevent.ui.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.adapter.FeedbackAdapter;
import com.fptu.fevent.model.EventFeedback;
import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.repository.EventFeedbackRepository;
import com.fptu.fevent.repository.EventInfoRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventInfoDetailActivity extends AppCompatActivity {
    private TextView tvName, tvStartTime, tvEndTime, tvLocation, tvDescription;
    private EventInfoRepository eventInfoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info_detail);

        tvName = findViewById(R.id.tv_event_name);
        tvStartTime = findViewById(R.id.tv_event_start_time);
        tvEndTime = findViewById(R.id.tv_event_end_time);
        tvLocation = findViewById(R.id.tv_event_location);
        tvDescription = findViewById(R.id.tv_event_description);

        eventInfoRepository = new EventInfoRepository(getApplication());

        int eventId = getIntent().getIntExtra("eventId", -1);
        if (eventId != -1) {
            loadEventInfo(eventId);
        } else {
            finish();
        }

        Button btnExport = findViewById(R.id.btn_export);
        btnExport.setOnClickListener(v -> {
            int currentUserId = getCurrentUserId();
            Intent intent = new Intent(this, ExportEventDataActivity.class);
            intent.putExtra("userId", currentUserId);
            startActivity(intent);
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        setupIncludedFeedbackList();
    }

    private void setupIncludedFeedbackList() {
        View includedLayout = findViewById(R.id.included_feedback_list);

        if (includedLayout != null) {
            RecyclerView recyclerView = includedLayout.findViewById(R.id.recycler_feedback);
            Button btnExport = includedLayout.findViewById(R.id.btn_export);
            Button btnBack = includedLayout.findViewById(R.id.btn_back);
            LinearLayout emptyLayout = includedLayout.findViewById(R.id.layout_empty_state);

            if (btnExport != null) {
                btnExport.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ExportEventDataActivity.class);
                    intent.putExtra("userId", getCurrentUserId());
                    startActivity(intent);
                });
            }

            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            // 4. Thiết lập RecyclerView
            if (recyclerView != null) {
                FeedbackAdapter adapter = new FeedbackAdapter(new ArrayList<>(), feedback -> {
                    // Xử lý khi click vào feedback item
                });
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

                int eventId = getIntent().getIntExtra("eventId", -1);
                if (eventId != -1) {
                    loadFeedbackForIncludedList(eventId, adapter, emptyLayout, recyclerView);
                }
            }
        }
    }

    private void loadFeedbackForIncludedList(int eventId, FeedbackAdapter adapter,
                                             LinearLayout emptyLayout, RecyclerView recyclerView) {
        new Thread(() -> {
            List<EventFeedback> feedbacks = new EventFeedbackRepository(getApplication())
                    .getByEventId(eventId);
            runOnUiThread(() -> {
                adapter.updateData(feedbacks);

                if (emptyLayout != null && recyclerView != null) {
                    emptyLayout.setVisibility(feedbacks.isEmpty() ? View.VISIBLE : View.GONE);
                    recyclerView.setVisibility(feedbacks.isEmpty() ? View.GONE : View.VISIBLE);
                }
            });
        }).start();
    }

    private int getCurrentUserId() {
        return 2;
    }

    public static void start(Context context, int eventId) {
        Intent intent = new Intent(context, EventInfoDetailActivity.class);
        intent.putExtra("eventId", eventId);
        context.startActivity(intent);
    }

    private void loadEventInfo(int eventId) {
        new Thread(() -> {
            EventInfo event = eventInfoRepository.getById(eventId);
            runOnUiThread(() -> {
                if (event != null) {
                    tvName.setText(event.name);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    tvStartTime.setText(event.start_time != null ? sdf.format(event.start_time) : "");
                    tvEndTime.setText(event.end_time != null ? sdf.format(event.end_time) : "");
                    tvLocation.setText(event.location != null ? event.location : "");
                    tvDescription.setText(event.description != null ? event.description : "");
                }
            });
        }).start();
    }
}