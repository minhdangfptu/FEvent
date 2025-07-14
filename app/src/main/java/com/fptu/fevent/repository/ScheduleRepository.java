package com.fptu.fevent.repository;

import android.app.Application;

import com.fptu.fevent.database.AppDatabase;
import com.fptu.fevent.model.Schedule;
import com.fptu.fevent.dao.ScheduleDao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScheduleRepository {
    private final ScheduleDao scheduleDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ScheduleRepository(Application application) {
        scheduleDao = AppDatabase.getInstance(application).scheduleDao();
    }

    public List<Schedule> getAll() {
        return scheduleDao.getAll();
    }

    public void insert(Schedule entity) {
        executor.execute(() -> scheduleDao.insert(entity));
    }

    public void update(Schedule entity) {
        executor.execute(() -> scheduleDao.update(entity));
    }

    public void delete(Schedule entity) {
        executor.execute(() -> scheduleDao.delete(entity));
    }
}