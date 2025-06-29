package com.fptu.fevent.repository;

import android.app.Application;

import com.fptu.fevent.model.Role;
import com.fptu.fevent.dao.RoleDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoleRepository {
    private final RoleDao roleDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public RoleRepository(Application application) {
        roleDao = com.fptu.fevent.database.AppDatabase.getInstance(application).roleDao();
    }

    public List<Role> getAll() {
        return roleDao.getAll();
    }

    public void insert(Role entity) {
        executor.execute(() -> roleDao.insert(entity));
    }

    public void update(Role entity) {
        executor.execute(() -> roleDao.update(entity));
    }

    public void delete(Role entity) {
        executor.execute(() -> roleDao.delete(entity));
    }
}
