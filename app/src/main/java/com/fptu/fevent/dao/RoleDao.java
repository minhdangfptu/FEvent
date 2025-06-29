package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.Role;

import java.util.List;

@Dao
public interface RoleDao {
    @Insert
    void insert(Role entity);

    @Update
    void update(Role entity);

    @Delete
    void delete(Role entity);

    @Query("SELECT * FROM role")
    List<Role> getAll();
}
