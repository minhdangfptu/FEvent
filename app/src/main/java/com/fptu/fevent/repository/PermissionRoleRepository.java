package com.fptu.fevent.repository;

import android.app.Application;

import com.fptu.fevent.model.PermissionRole;
import com.fptu.fevent.model.PermissionRoleDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PermissionRoleRepository {
    private final PermissionRoleDao permissionRoleDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PermissionRoleRepository(Application application) {
        permissionRoleDao = com.fptu.fevent.database.AppDatabase.getInstance(application).permissionRoleDao();
    }

    public List<PermissionRole> getAll() {
        return permissionRoleDao.getAll();
    }

    public void insert(PermissionRole entity) {
        executor.execute(() -> permissionRoleDao.insert(entity));
    }

    public void update(PermissionRole entity) {
        executor.execute(() -> permissionRoleDao.update(entity));
    }

    public void delete(PermissionRole entity) {
        executor.execute(() -> permissionRoleDao.delete(entity));
    }
}
