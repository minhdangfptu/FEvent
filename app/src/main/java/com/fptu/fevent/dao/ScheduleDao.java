package com.fptu.fevent.dao;

import androidx.room.*;
import com.fptu.fevent.model.Schedule;
import java.util.List;

@Dao
public interface ScheduleDao {

    @Insert
    long insert(Schedule entity);

    @Insert
    void insertAll(Schedule... entities);

    @Update
    void update(Schedule entity);

    @Delete
    void delete(Schedule entity);

    @Query("SELECT * FROM Schedule")
    List<Schedule> getAll();

    @Query("SELECT * FROM Schedule WHERE id = :id")
    Schedule getScheduleById(int id);

    @Query("SELECT * FROM Schedule ORDER BY start_time ASC")
    List<Schedule> getAllSchedules();

    @Query("SELECT COUNT(*) FROM Schedule")
    int getCount();
}
