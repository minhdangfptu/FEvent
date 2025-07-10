package com.fptu.fevent.ui.team;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.fevent.R;
import com.fptu.fevent.model.User;

public class UserAdapter extends ListAdapter<User, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private String teamName;
    private String teamDescription;

    public UserAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setHeaderData(String name, String description) {
        this.teamName = name;
        this.teamDescription = description;
        // Thông báo cho adapter vẽ lại item ở vị trí 0 (header)
        notifyItemChanged(0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_team_detail, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            ((HeaderViewHolder) holder).bind(teamName, teamDescription);
        } else {
            // Vị trí thực của user trong danh sách là position - 1 vì vị trí 0 là header
            User currentUser = getItem(position - 1);
            ((UserViewHolder) holder).bind(currentUser);
        }
    }

    @Override
    public int getItemCount() {
        // Tổng số item = số lượng user + 1 (cho header)
        return super.getItemCount() + 1;
    }

    // ViewHolder cho Header
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;
        HeaderViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.header_team_name);
            description = view.findViewById(R.id.header_team_description);
        }
        void bind(String teamName, String teamDescription) {
            name.setText(teamName);
            description.setText(teamDescription);
        }
    }

    // ViewHolder cho User Item
    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewFullname;
        private final TextView textViewEmail;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFullname = itemView.findViewById(R.id.text_view_user_fullname);
            textViewEmail = itemView.findViewById(R.id.text_view_user_email);
        }

        void bind(User user) {
            textViewFullname.setText(user.fullname);
            textViewEmail.setText(user.email);
        }
    }

    private static final DiffUtil.ItemCallback<User> DIFF_CALLBACK = new DiffUtil.ItemCallback<User>() {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.fullname.equals(newItem.fullname) &&
                    oldItem.email.equals(newItem.email);
        }
    };
}