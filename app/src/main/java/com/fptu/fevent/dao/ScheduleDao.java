package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.Permission;
import com.fptu.fevent.model.Schedule;
import com.fptu.fevent.model.Team;

import java.util.List;

@Dao
public interface ScheduleDao {
    @Insert
    void insert(Schedule entity);

    @Update
    void update(Schedule entity);

    @Delete
    void delete(Schedule entity);

    @Query("SELECT * FROM Schedule")
    List<Schedule> getAll();

    @Insert
    void insertAll(Permission... entities);

    @Query("SELECT COUNT(*) FROM Permission")
    int getCount();
}
