package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.EventFeedback;
import com.fptu.fevent.model.Team;


import java.util.List;

@Dao
public interface EventFeedbackDao {
    @Insert
    void insert(EventFeedback entity);

    @Update
    void update(EventFeedback entity);

    @Delete
    void delete(EventFeedback entity);

    @Query("SELECT * FROM EventFeedback")
    List<EventFeedback> getAll();
    @Insert
    void insertAll(EventFeedback... entities);

    @Query("SELECT COUNT(*) FROM EventFeedback")
    int getCount();
}
