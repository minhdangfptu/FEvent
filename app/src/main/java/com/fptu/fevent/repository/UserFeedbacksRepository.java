package com.fptu.fevent.repository;

import android.app.Application;

import com.fptu.fevent.model.UserFeedback;
import com.fptu.fevent.dao.UserFeedbackDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserFeedbacksRepository {
    private final UserFeedbackDao userFeedbackDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserFeedbacksRepository(Application application) {
        userFeedbackDao = com.fptu.fevent.database.AppDatabase.getInstance(application).userFeedbackDao();
    }

    public List<UserFeedback> getAll() {
        return userFeedbackDao.getAll();
    }

    public void insert(UserFeedback entity) {
        executor.execute(() -> userFeedbackDao.insert(entity));
    }

    public void update(UserFeedback entity) {
        executor.execute(() -> userFeedbackDao.update(entity));
    }

    public void delete(UserFeedback entity) {
        executor.execute(() -> userFeedbackDao.delete(entity));
    }
}
