package com.fptu.fevent.repository;

import android.app.Application;

import com.fptu.fevent.model.Team;
import com.fptu.fevent.dao.TeamDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeamRepository {
    private final TeamDao teamDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TeamRepository(Application application) {
        teamDao = com.fptu.fevent.database.AppDatabase.getInstance(application).teamDao();
    }

    public List<Team> getAll() {
        return teamDao.getAll();
    }

    public Team getTeamById(int teamId) {
        return teamDao.getById(teamId);
    }

    public void insert(Team entity) {
        executor.execute(() -> teamDao.insert(entity));
    }

    public void update(Team entity) {
        executor.execute(() -> teamDao.update(entity));
    }

    public void delete(Team entity) {
        executor.execute(() -> teamDao.delete(entity));
    }
    public void getAllAsync(java.util.function.Consumer<List<Team>> callback) {
        executor.execute(() -> {
            List<Team> result = teamDao.getAll();
            callback.accept(result);
        });
    }
    
    public List<Team> getAllTeams() {
        return teamDao.getAll();
    }
    public Team getTeamByIdSync(Integer teamId) {
        return teamDao.getById(teamId);
    }
}
