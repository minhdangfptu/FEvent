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

    @Query("SELECT * FROM Team WHERE id = :teamId LIMIT 1")
    Team getById(int teamId);

    @Insert
    void insertAll(Team... entities);

    @Query("SELECT COUNT(*) FROM Team")
    int getCount();
    @Query("SELECT name FROM Team WHERE id = :teamId LIMIT 1")
    String getTeamNameById(int teamId);
}
