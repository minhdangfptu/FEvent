package com.fptu.fevent.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.User;

import java.util.Date;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;
    private final UserActionListener listener;
    private int expandedPosition = -1;

    public interface UserActionListener {
        void onDisable(User user);

        void onDelete(User user);

        void onAssignRole(User user);

        void onAssignTeam(User user);
        void onViewInfo(User user);
    }

    public UserAdapter(List<User> userList, UserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvName.setText(user.getFullName());
        holder.tvEmail.setText(user.getEmail());

        // Hiển thị vai trò
        String roleText;
        switch (user.getRole_id()) {
            case 1:
                roleText = "Role: Quản trị viên";
                break;
            case 2:
                roleText = "Role: Trưởng ban Tổ chức";
                break;
            case 3:
                roleText = "Role: Trưởng ban";
                break;
            case 4:
                roleText = "Role: Thành viên";
                break;
            default:
                roleText = "Role: Không xác định";
        }
        holder.tvRole.setText(roleText);
        Date deactivatedUntil = user.getDeactivated_until();
        if (deactivatedUntil != null) {
            holder.tvActive.setText("Vô hiệu hóa đến: " + user.getDeactivated_until());
        }else {
            holder.tvActive.setText("Đang hoạt động");
        }

        boolean isExpanded = position == expandedPosition;
        holder.actionsLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            expandedPosition = isExpanded ? -1 : position;
            notifyDataSetChanged();
        });

        holder.btnDisable.setOnClickListener(v -> listener.onDisable(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(user));
        holder.btnRole.setOnClickListener(v -> listener.onAssignRole(user));
        holder.btnTeam.setOnClickListener(v -> listener.onAssignTeam(user));
        holder.btnViewInfo.setOnClickListener(v -> listener.onViewInfo(user));
    }

    public void updateList(List<User> newList) {
        this.userList.clear();
        this.userList.addAll(newList);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole, tvActive;
        LinearLayout actionsLayout;
        LinearLayout btnDisable, btnDelete, btnRole, btnTeam, btnViewInfo;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            tvRole = itemView.findViewById(R.id.tv_user_role);
            tvActive = itemView.findViewById(R.id.tv_user_deactivated);
            actionsLayout = itemView.findViewById(R.id.layout_actions);
            btnDisable = itemView.findViewById(R.id.action_disable);
            btnDelete = itemView.findViewById(R.id.action_delete);
            btnRole = itemView.findViewById(R.id.action_assign_role);
            btnTeam = itemView.findViewById(R.id.action_assign_team);
            btnViewInfo = itemView.findViewById(R.id.action_aview_info);

        }
    }
}
