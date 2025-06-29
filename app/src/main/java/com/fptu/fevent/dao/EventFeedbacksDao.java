package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.EventFeedbacks;

import java.util.List;

@Dao
public interface EventFeedbacksDao {
    @Insert
    void insert(EventFeedbacks entity);

    @Update
    void update(EventFeedbacks entity);

    @Delete
    void delete(EventFeedbacks entity);

    @Query("SELECT * FROM eventfeedbacks")
    List<EventFeedbacks> getAll();
}
