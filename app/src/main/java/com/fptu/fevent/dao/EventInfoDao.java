package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.model.Team;

import java.util.List;

@Dao
public interface EventInfoDao {
    @Insert
    void insert(EventInfo entity);

    @Update
    void update(EventInfo entity);

    @Delete
    void delete(EventInfo entity);

    @Query("SELECT * FROM EventInfo")
    List<EventInfo> getAll();

    @Insert
    void insertAll(EventInfo... entities);

    @Query("SELECT COUNT(*) FROM EventInfo")
    int getCount();

    @Query("SELECT * FROM EventInfo WHERE id = :eventId LIMIT 1")
    EventInfo getById(int eventId);
}
