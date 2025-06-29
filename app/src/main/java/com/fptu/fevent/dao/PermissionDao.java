package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.Permission;

import java.util.List;

@Dao
public interface PermissionDao {
    @Insert
    void insert(Permission entity);

    @Update
    void update(Permission entity);

    @Delete
    void delete(Permission entity);

    @Query("SELECT * FROM permission")
    List<Permission> getAll();
}
