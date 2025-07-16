package com.fptu.fevent.repository;

import android.app.Application;

import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.dao.EventInfoDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventInfoRepository {
    private final EventInfoDao eventInfoDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public EventInfoRepository(Application application) {
        eventInfoDao = com.fptu.fevent.database.AppDatabase.getInstance(application).eventInfoDao();
    }

    public List<EventInfo> getAll() {
        return eventInfoDao.getAll();
    }

    public void insert(EventInfo entity) {
        executor.execute(() -> eventInfoDao.insert(entity));
    }

    public void update(EventInfo entity) {
        executor.execute(() -> eventInfoDao.update(entity));
    }

    public void delete(EventInfo entity) {
        executor.execute(() -> eventInfoDao.delete(entity));
    }
    public EventInfo getById(int eventId) {
        return eventInfoDao.getById(eventId);
    }


    public List<EventInfo> getAllEventsSync() {
        return eventInfoDao.getAll();
    }

}
