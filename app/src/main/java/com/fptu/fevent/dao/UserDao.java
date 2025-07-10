package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.User;

import java.util.List;
import androidx.lifecycle.LiveData;

@Dao
public interface UserDao {
    @Insert
    void insert(User entity);

    @Update
    void update(User entity);

    @Delete
    void delete(User entity);

    @Query("SELECT * FROM User")
    LiveData<List<User>> getAll();

    // THÊM PHƯƠG THỨC MỚI NÀY
    @Query("SELECT * FROM User WHERE team_id = :teamId")
    LiveData<List<User>> getUsersInTeam(int teamId);

    // THÊM PHƯƠNG THỨC MỚI NÀY
    @Query("SELECT * FROM User WHERE team_id IS NULL")
    LiveData<List<User>> getUnassignedUsers();
}
