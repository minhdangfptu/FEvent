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
import com.fptu.fevent.model.Team;
import com.fptu.fevent.ui.team.EditTeamActivity;
import com.fptu.fevent.ui.team.DeleteTeamActivity;
import com.fptu.fevent.ui.team.TeamDetailActivity;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {
    private List<Team> teams;
    private Context context;

    public TeamAdapter(Context context, List<Team> teams) {
        this.context = context;
        this.teams = teams;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teams.get(position);
        holder.bind(team);
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    public void updateTeams(List<Team> newTeams) {
        this.teams = newTeams;
        notifyDataSetChanged();
    }

    class TeamViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTeamName, tvTeamDescription;
        private Button btnEdit, btnDelete, btnViewDetail;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTeamName = itemView.findViewById(R.id.tv_team_name);
            tvTeamDescription = itemView.findViewById(R.id.tv_team_description);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnViewDetail = itemView.findViewById(R.id.btn_view_detail);
        }

        public void bind(Team team) {
            tvTeamName.setText(team.name);
            tvTeamDescription.setText(team.description);

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditTeamActivity.class);
                intent.putExtra("team_id", team.id);
                context.startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                Intent intent = new Intent(context, DeleteTeamActivity.class);
                intent.putExtra("team_id", team.id);
                context.startActivity(intent);
            });

            btnViewDetail.setOnClickListener(v -> {
                Intent intent = new Intent(context, TeamDetailActivity.class);
                intent.putExtra("team_id", team.id);
                context.startActivity(intent);
            });
        }
    }
}

