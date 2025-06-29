package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.UserFeedbacks;

import java.util.List;

@Dao
public interface UserFeedbacksDao {
    @Insert
    void insert(UserFeedbacks entity);

    @Update
    void update(UserFeedbacks entity);

    @Delete
    void delete(UserFeedbacks entity);

    @Query("SELECT * FROM userfeedbacks")
    List<UserFeedbacks> getAll();
}
