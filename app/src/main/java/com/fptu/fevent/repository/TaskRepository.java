package com.fptu.fevent.repository;

import android.app.Application;

import com.fptu.fevent.model.Task;
import com.fptu.fevent.model.TaskDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {
    private final TaskDao taskDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TaskRepository(Application application) {
        taskDao = com.fptu.fevent.database.AppDatabase.getInstance(application).taskDao();
    }

    public List<Task> getAll() {
        return taskDao.getAll();
    }

    public void insert(Task entity) {
        executor.execute(() -> taskDao.insert(entity));
    }

    public void update(Task entity) {
        executor.execute(() -> taskDao.update(entity));
    }

    public void delete(Task entity) {
        executor.execute(() -> taskDao.delete(entity));
    }
}
