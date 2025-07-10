package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.Permission;
import com.fptu.fevent.model.Team;

import java.util.List;

@Dao
public interface PermissionDao {
    @Insert
    void insert(Permission entity);

    @Update
    void update(Permission entity);

    @Delete
    void delete(Permission entity);

    @Query("SELECT * FROM Permission")
    List<Permission> getAll();

    @Insert
    void insertAll(Permission... entities);

    @Query("SELECT COUNT(*) FROM Permission")
    int getCount();
}
