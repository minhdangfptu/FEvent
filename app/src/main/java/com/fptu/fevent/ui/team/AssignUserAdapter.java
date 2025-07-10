package com.fptu.fevent.ui.team;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.fevent.R;
import com.fptu.fevent.model.User;
import java.util.HashSet;
import java.util.Set;

public class AssignUserAdapter extends ListAdapter<User, AssignUserAdapter.UserViewHolder> {

    private final Set<User> selectedUsers = new HashSet<>();

    public AssignUserAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_selectable, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User currentUser = getItem(position);
        holder.bind(currentUser);
    }

    public Set<User> getSelectedUsers() {
        return selectedUsers;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewFullname;
        private final TextView textViewEmail;
        private final CheckBox checkBox;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ View từ layout
            textViewFullname = itemView.findViewById(R.id.text_view_user_fullname_selectable);
            textViewEmail = itemView.findViewById(R.id.text_view_user_email_selectable);
            checkBox = itemView.findViewById(R.id.checkbox_assign_user);
        }

        public void bind(final User user) {
            textViewFullname.setText(user.fullname);
            textViewEmail.setText(user.email);
            checkBox.setChecked(selectedUsers.contains(user));

            itemView.setOnClickListener(v -> {
                if (selectedUsers.contains(user)) {
                    selectedUsers.remove(user);
                    checkBox.setChecked(false);
                } else {
                    selectedUsers.add(user);
                    checkBox.setChecked(true);
                }
            });
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