package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task entity);

    @Update
    void update(Task entity);

    @Delete
    void delete(Task entity);

    @Query("SELECT * FROM Task")
    List<Task> getAll();
}
