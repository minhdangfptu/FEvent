package com.fptu.fevent.ui.feedback;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.fevent.R;
import com.fptu.fevent.model.UserFeedback;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserFeedbackAdapter extends ListAdapter<UserFeedback, UserFeedbackAdapter.FeedbackViewHolder> {

    public UserFeedbackAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewInfo, textViewComment, textViewDate;
        private final RatingBar ratingBar;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewInfo = itemView.findViewById(R.id.text_view_feedback_info);
            textViewComment = itemView.findViewById(R.id.text_view_feedback_comment);
            textViewDate = itemView.findViewById(R.id.text_view_feedback_date);
            ratingBar = itemView.findViewById(R.id.rating_bar_feedback);
        }

        public void bind(UserFeedback feedback) {
            // NOTE: Để hiển thị tên, cần query từ DB. Tạm thời hiển thị ID.
            textViewInfo.setText("Đánh giá từ User " + feedback.from_user_id + " đến User " + feedback.to_user_id);
            textViewComment.setText(feedback.comment);
            ratingBar.setRating(feedback.rating);
            if (feedback.created_at != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                textViewDate.setText("Vào ngày: " + sdf.format(feedback.created_at));
            }
        }
    }

    private static final DiffUtil.ItemCallback<UserFeedback> DIFF_CALLBACK = new DiffUtil.ItemCallback<UserFeedback>() {
        @Override
        public boolean areItemsTheSame(@NonNull UserFeedback oldItem, @NonNull UserFeedback newItem) {
            return oldItem.id == newItem.id;
        }
        @Override
        public boolean areContentsTheSame(@NonNull UserFeedback oldItem, @NonNull UserFeedback newItem) {
            return oldItem.comment.equals(newItem.comment) && oldItem.rating == newItem.rating;
        }
    };
}