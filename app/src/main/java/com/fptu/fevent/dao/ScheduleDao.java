package com.fptu.fevent.dao;

import androidx.room.*;
import com.fptu.fevent.model.Schedule;
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
}