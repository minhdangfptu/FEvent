package com.fptu.fevent.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.fevent.R;
import com.fptu.fevent.model.EventInfo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventInfoAdapter extends RecyclerView.Adapter<EventInfoAdapter.EventViewHolder> {
    private final List<EventInfo> eventList;
    private final OnItemClickListener listener;
    private final OnFeedbackClickListener feedbackListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(EventInfo event);
    }

    public interface OnFeedbackClickListener {
        void onFeedbackClick(EventInfo event);
    }

    public EventInfoAdapter(List<EventInfo> eventList,
                            OnItemClickListener listener,
                            OnFeedbackClickListener feedbackListener) {
        this.eventList = eventList;
        this.listener = listener;
        this.feedbackListener = feedbackListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_info, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventInfo event = eventList.get(position);
        holder.bind(event, listener, feedbackListener, dateFormat);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvLocation, tvStartDate, tvEndDate, tvDescription;
        Button btnFeedback;

        EventViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tv_event_name);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvStartDate = itemView.findViewById(R.id.tv_start_date);
            tvEndDate = itemView.findViewById(R.id.tv_end_date);
            tvDescription = itemView.findViewById(R.id.tv_description);
            btnFeedback = itemView.findViewById(R.id.btn_feedback);
        }

        public void bind(final EventInfo event,
                         final OnItemClickListener listener,
                         final OnFeedbackClickListener feedbackListener,
                         SimpleDateFormat dateFormat) {
            // Set event data
            tvEventName.setText(event.name);
            tvLocation.setText(event.location);
            tvStartDate.setText(event.start_time != null ?
                    "Bắt đầu: " + dateFormat.format(event.start_time) : "Chưa có thời gian bắt đầu");
            tvEndDate.setText(event.end_time != null ?
                    "Kết thúc: " + dateFormat.format(event.end_time) : "Chưa có thời gian kết thúc");
            tvDescription.setText(event.description != null ?
                    event.description : "Không có mô tả");

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(event);
                }
            });

            btnFeedback.setOnClickListener(v -> {
                if (feedbackListener != null) {
                    feedbackListener.onFeedbackClick(event);
                }
            });
        }
    }
}