package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.PermissionRole;

import java.util.List;

@Dao
public interface PermissionRoleDao {
    @Insert
    void insert(PermissionRole entity);

    @Update
    void update(PermissionRole entity);

    @Delete
    void delete(PermissionRole entity);

    @Query("SELECT * FROM permissionrole")
    List<PermissionRole> getAll();
}
