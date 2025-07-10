package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.Role;
import com.fptu.fevent.model.Team;

import java.util.List;

@Dao
public interface TeamDao {
    @Insert
    void insert(Team entity);

    @Update
    void update(Team entity);

    @Delete
    void delete(Team entity);

    @Query("SELECT * FROM Team")
    List<Team> getAll();

    @Insert
    void insertAll(Team... entities);

    @Query("SELECT COUNT(*) FROM Team")
    int getCount();
}
