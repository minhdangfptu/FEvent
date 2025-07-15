package com.fptu.fevent.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.fevent.R;
import com.fptu.fevent.model.EventInfo;
import java.util.List;

public class EventInfoAdapter extends RecyclerView.Adapter<EventInfoAdapter.EventViewHolder> {
    private final List<EventInfo> eventList;

    public EventInfoAdapter(List<EventInfo> eventList) {
        this.eventList = eventList;
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
        holder.txtTitle.setText(event.name);
        holder.txtDescription.setText(event.description);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDescription;

        EventViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_event_title);
            txtDescription = itemView.findViewById(R.id.txt_event_description);
        }
    }
}