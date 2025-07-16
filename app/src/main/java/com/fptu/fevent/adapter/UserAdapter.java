package com.fptu.fevent.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.User;
import com.fptu.fevent.ui.team.TeamMembersActivity;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users;
    private Context context;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUsers(List<User> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName, tvUserEmail, tvUserPosition;
        private Button btnViewDetail;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
            tvUserPosition = itemView.findViewById(R.id.tv_user_position);
            btnViewDetail = itemView.findViewById(R.id.btn_view_detail);
        }

        public void bind(User user) {
            tvUserName.setText(user.fullname != null ? user.fullname : "N/A");
            tvUserEmail.setText(user.email != null ? user.email : "N/A");
            tvUserPosition.setText(user.position != null ? user.position : "N/A");

            btnViewDetail.setOnClickListener(v -> {
                Intent intent = new Intent(context, TeamMembersActivity.class);
                intent.putExtra("user_id", user.id);
                context.startActivity(intent);
            });
        }
    }
}

