package com.fptu.fevent.repository;

import android.app.Application;

import com.fptu.fevent.dao.EventFeedback;
import com.fptu.fevent.dao.EventFeedbackDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventFeedbacksRepository {
    private final EventFeedbacksDao eventFeedbacksDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public EventFeedbacksRepository(Application application) {
        eventFeedbacksDao = com.fptu.fevent.database.AppDatabase.getInstance(application).eventFeedbacksDao();
    }

    public List<EventFeedbacks> getAll() {
        return eventFeedbacksDao.getAll();
    }

    public void insert(EventFeedbacks entity) {
        executor.execute(() -> eventFeedbacksDao.insert(entity));
    }

    public void update(EventFeedbacks entity) {
        executor.execute(() -> eventFeedbacksDao.update(entity));
    }

    public void delete(EventFeedbacks entity) {
        executor.execute(() -> eventFeedbacksDao.delete(entity));
    }
}
