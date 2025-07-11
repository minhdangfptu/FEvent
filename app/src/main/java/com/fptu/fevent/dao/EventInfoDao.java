package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.EventInfo;

import java.util.List;

import androidx.lifecycle.LiveData;

@Dao
public interface EventInfoDao {
    @Insert
    void insert(EventInfo entity);

    @Update
    void update(EventInfo entity);

    @Delete
    void delete(EventInfo entity);

    @Query("SELECT * FROM EventInfo")
    LiveData<List<EventInfo>> getAll();
}
