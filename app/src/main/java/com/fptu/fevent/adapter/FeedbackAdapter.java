package com.fptu.fevent.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // FIX: Import TextView

import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.EventFeedback;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    private final List<EventFeedback> feedbackList;
    private final FeedbackActionListener listener;

    public interface FeedbackActionListener {
        void onFeedbackClick(EventFeedback feedback);
    }

    public FeedbackAdapter(List<EventFeedback> feedbackList, FeedbackActionListener listener) {
        this.feedbackList = feedbackList;
        this.listener = listener;
    }

    @Override
    public FeedbackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(FeedbackViewHolder holder, int position) {
        EventFeedback feedback = feedbackList.get(position);
        holder.tvRating.setText("Điểm: " + feedback.rating);
        holder.tvComment.setText(feedback.comment);
        holder.itemView.setOnClickListener(v -> listener.onFeedbackClick(feedback));
    }

    public void updateData(List<EventFeedback> newFeedbacks) {
        this.feedbackList.clear();
        this.feedbackList.addAll(newFeedbacks);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView tvRating, tvComment;

        public FeedbackViewHolder(View itemView) {
            super(itemView);
            tvRating = itemView.findViewById(R.id.tv_feedback_rating);
            tvComment = itemView.findViewById(R.id.tv_feedback_comment);
        }
    }
}