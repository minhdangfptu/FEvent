package com.fptu.fevent.repository;

import android.app.Application;

import com.fptu.fevent.model.Task;
import com.fptu.fevent.dao.TaskDao;

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

    public Task getTaskById(int id) {
        return taskDao.getTaskById(id);
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

    // Async methods for better performance
    public void getAllAsync(java.util.function.Consumer<List<Task>> callback) {
        executor.execute(() -> {
            List<Task> result = taskDao.getAll();
            callback.accept(result);
        });
    }

    public void insertAsync(Task task, java.util.function.Consumer<Long> callback) {
        executor.execute(() -> {
            long id = taskDao.insert(task);
            callback.accept(id);
        });
    }

    public void updateAsync(Task task, Runnable callback) {
        executor.execute(() -> {
            taskDao.update(task);
            if (callback != null) {
                callback.run();
            }
        });
    }

    public void deleteAsync(Task task, Runnable callback) {
        executor.execute(() -> {
            taskDao.delete(task);
            if (callback != null) {
                callback.run();
            }
        });
    }

    // Get tasks by assignment
    public void getTasksByUserId(int userId, java.util.function.Consumer<List<Task>> callback) {
        executor.execute(() -> {
            List<Task> result = taskDao.getTasksByUserId(userId);
            callback.accept(result);
        });
    }

    public void getTasksByTeamId(int teamId, java.util.function.Consumer<List<Task>> callback) {
        executor.execute(() -> {
            List<Task> result = taskDao.getTasksByTeamId(teamId);
            callback.accept(result);
        });
    }

    // Get tasks by status
    public void getTasksByStatus(String status, java.util.function.Consumer<List<Task>> callback) {
        executor.execute(() -> {
            List<Task> result = taskDao.getTasksByStatus(status);
            callback.accept(result);
        });
    }
}
