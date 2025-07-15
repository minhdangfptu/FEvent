package com.fptu.fevent.repository;

import android.app.Application;

import com.fptu.fevent.model.EventFeedback;
import com.fptu.fevent.dao.EventFeedbackDao;
import com.fptu.fevent.database.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventFeedbackRepository {
    private final EventFeedbackDao eventFeedbackDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public EventFeedbackRepository(Application application) {
        eventFeedbackDao = AppDatabase.getInstance(application).eventFeedbackDao();
    }

    public List<EventFeedback> getAll() {
        return eventFeedbackDao.getAll();
    }

    public void insert(EventFeedback entity) {
        executor.execute(() -> eventFeedbackDao.insert(entity));
    }

    public void update(EventFeedback entity) {
        executor.execute(() -> eventFeedbackDao.update(entity));
    }

    public void delete(EventFeedback entity) {
        executor.execute(() -> eventFeedbackDao.delete(entity));
    }

    public List<EventFeedback> getByEventId(int eventId) {
        return eventFeedbackDao.getByEventId(eventId);
    }

    public List<EventFeedback> getByUserAndEvent(int userId, int eventId) {
        return eventFeedbackDao.getByUserAndEvent(userId, eventId);
    }

    public EventFeedback getById(int feedbackId) {
        return eventFeedbackDao.getById(feedbackId);
    }
}
