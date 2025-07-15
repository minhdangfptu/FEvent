package com.fptu.fevent.dao;

import androidx.room.*;
import com.fptu.fevent.model.Schedule;
import java.util.List;

@Dao
public interface ScheduleDao {
    @Insert
    long insert(Schedule entity);

    @Update
    void update(Schedule entity);

    @Delete
    void delete(Schedule entity);

    @Query("SELECT * FROM Schedule")
    List<Schedule> getAll();
    
    @Insert
    void insertAll(Schedule... entities);

    @Query("SELECT COUNT(*) FROM Schedule")
    int getCount();
}