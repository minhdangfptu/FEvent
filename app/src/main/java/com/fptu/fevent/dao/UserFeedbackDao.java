package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.UserFeedback;

import java.util.List;

@Dao
public interface UserFeedbackDao {
    @Insert
    void insert(UserFeedback entity);

    @Update
    void update(UserFeedback entity);

    @Delete
    void delete(UserFeedback entity);

    @Query("SELECT * FROM UserFeedback")
    List<UserFeedback> getAll();
}
