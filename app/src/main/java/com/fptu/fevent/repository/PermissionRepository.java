package com.fptu.fevent.repository;

import android.app.Application;

import com.fptu.fevent.model.Permission;
import com.fptu.fevent.dao.PermissionDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PermissionRepository {
    private final PermissionDao permissionDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PermissionRepository(Application application) {
        permissionDao = com.fptu.fevent.database.AppDatabase.getInstance(application).permissionDao();
    }

    public List<Permission> getAll() {
        return permissionDao.getAll();
    }

    public void insert(Permission entity) {
        executor.execute(() -> permissionDao.insert(entity));
    }

    public void update(Permission entity) {
        executor.execute(() -> permissionDao.update(entity));
    }

    public void delete(Permission entity) {
        executor.execute(() -> permissionDao.delete(entity));
    }
}
