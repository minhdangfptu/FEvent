package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.Team;

import java.util.List;

import androidx.lifecycle.LiveData;

@Dao
public interface TeamDao {
    @Insert
    void insert(Team entity);

    @Update
    void update(Team entity);

    @Delete
    void delete(Team entity);

    @Query("SELECT * FROM Team")
    LiveData<List<Team>> getAllTeams();
}
