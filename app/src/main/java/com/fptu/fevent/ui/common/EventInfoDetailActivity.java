package com.fptu.fevent.ui.common;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.fptu.fevent.R;
import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.repository.EventInfoRepository;
import java.text.SimpleDateFormat;
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

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
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