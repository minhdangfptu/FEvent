package com.fptu.fevent.model;

import androidx.room.*;
import com.fptu.fevent.util.DateConverter;
import java.util.Date;

@Entity(
        tableName = "Notification",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Task.class,
                        parentColumns = "id",
                        childColumns = "task_id",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
@TypeConverters(DateConverter.class)
public class Notification {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(index = true)
    public int user_id;

    @ColumnInfo(index = true)
    public Integer task_id; // nullable for non-task notifications

    public String title;
    public String message;
    public String type; // "TASK_DELAY", "TASK_OVERDUE", "TASK_REMINDER", "GENERAL"
    public String priority; // "LOW", "MEDIUM", "HIGH", "URGENT"
    public boolean is_read;
    public Date created_at;
    public Date read_at;

    // Default constructor
    public Notification() {
        this.is_read = false;
        this.created_at = new Date();
    }

    // Constructor with parameters
    @Ignore
    public Notification(int user_id, Integer task_id, String title, String message, String type, String priority) {
        this.user_id = user_id;
        this.task_id = task_id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.is_read = false;
        this.created_at = new Date();
    }
} 