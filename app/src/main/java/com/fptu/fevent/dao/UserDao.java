package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User entity);

    @Update
    void update(User entity);

    @Delete
    void delete(User entity);

    @Query("SELECT * FROM User")
    List<User> getAll();
}
