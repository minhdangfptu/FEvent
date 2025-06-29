package com.fptu.fevent.repository;

import android.app.Application;

import com.fptu.fevent.model.User;
import com.fptu.fevent.model.UserDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserRepository(Application application) {
        userDao = com.fptu.fevent.database.AppDatabase.getInstance(application).userDao();
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public void insert(User entity) {
        executor.execute(() -> userDao.insert(entity));
    }

    public void update(User entity) {
        executor.execute(() -> userDao.update(entity));
    }

    public void delete(User entity) {
        executor.execute(() -> userDao.delete(entity));
    }
}
