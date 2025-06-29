package com.fptu.fevent.repository;

import android.app.Application;

import com.fptu.fevent.model.UserFeedbacks;
import com.fptu.fevent.model.UserFeedbacksDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserFeedbacksRepository {
    private final UserFeedbacksDao userFeedbacksDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserFeedbacksRepository(Application application) {
        userFeedbacksDao = com.fptu.fevent.database.AppDatabase.getInstance(application).userFeedbacksDao();
    }

    public List<UserFeedbacks> getAll() {
        return userFeedbacksDao.getAll();
    }

    public void insert(UserFeedbacks entity) {
        executor.execute(() -> userFeedbacksDao.insert(entity));
    }

    public void update(UserFeedbacks entity) {
        executor.execute(() -> userFeedbacksDao.update(entity));
    }

    public void delete(UserFeedbacks entity) {
        executor.execute(() -> userFeedbacksDao.delete(entity));
    }
}
