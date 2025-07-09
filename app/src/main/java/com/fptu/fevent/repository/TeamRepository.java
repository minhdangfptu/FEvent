// Mở file này và chỉnh sửa
package com.fptu.fevent.repository;

import android.app.Application;
import androidx.lifecycle.LiveData; // <-- Thêm import
import com.fptu.fevent.model.Team;
import com.fptu.fevent.dao.TeamDao;
import com.fptu.fevent.database.AppDatabase;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeamRepository {
    private final TeamDao teamDao;
    private final LiveData<List<Team>> allTeams; // <-- Thêm dòng này
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TeamRepository(Application application) {
        AppDatabase database = com.fptu.fevent.database.AppDatabase.getInstance(application);
        teamDao = database.teamDao();
        allTeams = teamDao.getAllTeams(); // <-- Thêm dòng này
    }

    // Sửa phương thức này
    public LiveData<List<Team>> getAllTeams() {
        return allTeams;
    }

    // Các phương thức còn lại giữ nguyên
    public void insert(Team entity) {
        executor.execute(() -> teamDao.insert(entity));
    }

    public void update(Team entity) {
        executor.execute(() -> teamDao.update(entity));
    }

    public void delete(Team entity) {
        executor.execute(() -> teamDao.delete(entity));
    }
}