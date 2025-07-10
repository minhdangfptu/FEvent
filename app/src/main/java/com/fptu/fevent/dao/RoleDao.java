package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.Role;

import java.util.List;

@Dao
public interface RoleDao {
    @Insert
    void insert(Role entity);

    @Insert
    void insertAll(Role... entities);

    @Update
    void update(Role entity);

    @Delete
    void delete(Role entity);

    @Query("SELECT * FROM Role")
    List<Role> getAll();

    @Query("SELECT COUNT(*) FROM Role")
    int getCount();
}
