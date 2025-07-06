package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User entity);

    @Update
    void update(User entity);

    @Delete
    void delete(User entity);

    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);

    @Query("SELECT COUNT(*) FROM User WHERE email = :email")
    int countByEmail(String email);
}
