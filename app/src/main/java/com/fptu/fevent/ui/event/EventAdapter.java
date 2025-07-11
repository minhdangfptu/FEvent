package com.fptu.fevent.ui.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.EventInfo;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventAdapter extends ListAdapter<EventInfo, EventAdapter.EventViewHolder> {

    private OnItemClickListener listener;

    public EventAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventInfo currentEvent = getItem(position);
        holder.bind(currentEvent);
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewTime;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_event_name);
            textViewTime = itemView.findViewById(R.id.text_view_event_time);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }

        public void bind(EventInfo eventInfo) {
            textViewName.setText(eventInfo.name);
            if (eventInfo.start_time != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                textViewTime.setText(sdf.format(eventInfo.start_time));
            } else {
                textViewTime.setText("Chưa có ngày");
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(EventInfo eventInfo);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<EventInfo> DIFF_CALLBACK = new DiffUtil.ItemCallback<EventInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull EventInfo oldItem, @NonNull EventInfo newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull EventInfo oldItem, @NonNull EventInfo newItem) {
            return oldItem.name.equals(newItem.name) &&
                    oldItem.description.equals(newItem.description) &&
                    oldItem.start_time.equals(newItem.start_time);
        }
    };
}