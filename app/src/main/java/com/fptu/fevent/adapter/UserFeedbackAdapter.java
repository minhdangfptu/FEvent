package com.fptu.fevent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.database.AppDatabase;
import com.fptu.fevent.model.User;
import com.fptu.fevent.model.UserFeedback;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserFeedbackAdapter extends RecyclerView.Adapter<UserFeedbackAdapter.FeedbackViewHolder> {
    private List<UserFeedback> feedbacks;
    private Context context;
    private boolean isOwnFeedbackView;
    private AppDatabase database;
    private ExecutorService executorService;

    public UserFeedbackAdapter(Context context, List<UserFeedback> feedbacks, boolean isOwnFeedbackView) {
        this.context = context;
        this.feedbacks = feedbacks;
        this.isOwnFeedbackView = isOwnFeedbackView;
        this.database = AppDatabase.getInstance(context);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        UserFeedback feedback = feedbacks.get(position);
        holder.bind(feedback);
    }

    @Override
    public int getItemCount() {
        return feedbacks.size();
    }

    public void updateFeedbacks(List<UserFeedback> newFeedbacks) {
        this.feedbacks = newFeedbacks;
        notifyDataSetChanged();
    }

    class FeedbackViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFromUser, tvToUser, tvComment, tvDate;
        private RatingBar ratingBar;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFromUser = itemView.findViewById(R.id.tv_from_user);
            tvToUser = itemView.findViewById(R.id.tv_to_user);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvDate = itemView.findViewById(R.id.tv_date);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }

        public void bind(UserFeedback feedback) {
            ratingBar.setRating(feedback.rating);
            tvComment.setText(feedback.comment);
            
            if (feedback.created_at != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                tvDate.setText(sdf.format(feedback.created_at));
            } else {
                tvDate.setText("N/A");
            }

            // Load user names asynchronously
            executorService.execute(() -> {
                try {
                    User fromUser = database.userDao().getById(feedback.from_user_id);
                    User toUser = database.userDao().getById(feedback.to_user_id);
                    
                    String fromUserName = fromUser != null ? fromUser.fullname : "N/A";
                    String toUserName = toUser != null ? toUser.fullname : "N/A";
                    
                    // Update UI on main thread
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (isOwnFeedbackView) {
                            // In own feedback view, show who gave the feedback
                            tvFromUser.setText("Từ: " + fromUserName);
                            tvToUser.setVisibility(View.GONE);
                        } else {
                            // In general feedback list, show both
                            tvFromUser.setText("Từ: " + fromUserName);
                            tvToUser.setText("Cho: " + toUserName);
                        }
                    });
                } catch (Exception e) {
                    // Handle error silently or log it
                }
            });
        }
    }
}

