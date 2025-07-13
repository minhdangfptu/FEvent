package com.fptu.fevent.dao;

import androidx.room.*;

import com.fptu.fevent.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long insert(Task entity);

    @Update
    void update(Task entity);

    @Delete
    void delete(Task entity);

    @Query("SELECT * FROM Task ORDER BY due_date ASC")
    List<Task> getAll();

    @Insert
    void insertAll(Task... entities);

    @Query("SELECT COUNT(*) FROM Task")
    int getCount();

    // Get tasks by user assignment
    @Query("SELECT * FROM Task WHERE assigned_to = :userId ORDER BY due_date ASC")
    List<Task> getTasksByUserId(int userId);

    // Get tasks by team assignment
    @Query("SELECT * FROM Task WHERE team_id = :teamId ORDER BY due_date ASC")
    List<Task> getTasksByTeamId(int teamId);

    // Get tasks by status
    @Query("SELECT * FROM Task WHERE status = :status ORDER BY due_date ASC")
    List<Task> getTasksByStatus(String status);

    // Get task by ID
    @Query("SELECT * FROM Task WHERE id = :taskId LIMIT 1")
    Task getTaskById(int taskId);

    // Update task status
    @Query("UPDATE Task SET status = :status WHERE id = :taskId")
    void updateTaskStatus(int taskId, String status);

    // Get overdue tasks
    @Query("SELECT * FROM Task WHERE due_date < :currentDate AND status != 'Hoàn thành' ORDER BY due_date ASC")
    List<Task> getOverdueTasks(long currentDate);

    // Get tasks for current user (assigned to them or their team)
    @Query("SELECT * FROM Task WHERE assigned_to = :userId OR team_id = :teamId ORDER BY due_date ASC")
    List<Task> getTasksForUser(int userId, int teamId);

    // Count tasks by status
    @Query("SELECT COUNT(*) FROM Task WHERE status = :status")
    int countTasksByStatus(String status);
}
