package com.fptu.fevent.ui.event;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.viewmodel.EventInfoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EventListActivity extends AppCompatActivity {

    private EventInfoViewModel eventInfoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        setTitle("Danh sách Sự kiện");

        // Nút thêm sự kiện
        FloatingActionButton fabAddEvent = findViewById(R.id.fab_add_event);
        fabAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(EventListActivity.this, AddEditEventActivity.class);
            startActivity(intent);
        });

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_event);
        final EventAdapter adapter = new EventAdapter();
        recyclerView.setAdapter(adapter);

        // ViewModel
        eventInfoViewModel = new ViewModelProvider(this).get(EventInfoViewModel.class);
        eventInfoViewModel.getAllEvents().observe(this, adapter::submitList);

        // Xử lý click để sửa sự kiện
        adapter.setOnItemClickListener(eventInfo -> {
            Intent intent = new Intent(EventListActivity.this, AddEditEventActivity.class);
            intent.putExtra(AddEditEventActivity.EXTRA_EVENT_ID, eventInfo.id);
            intent.putExtra(AddEditEventActivity.EXTRA_EVENT_NAME, eventInfo.name);
            intent.putExtra(AddEditEventActivity.EXTRA_EVENT_DESC, eventInfo.description);
            startActivity(intent);
        });

        // Bạn có thể thêm ItemTouchHelper để vuốt xóa sự kiện tương tự như TeamListActivity
    }
}