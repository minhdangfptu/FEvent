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
                ),
                @ForeignKey(
                        entity = Schedule.class,
                        parentColumns = "id",
                        childColumns = "schedule_id",
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
    public Integer task_id;

    @ColumnInfo(index = true)
    public Integer schedule_id;

    public String title;
    public String message;
    public String type; // "TASK_DELAY", "TASK_OVERDUE", "TASK_REMINDER", "TASK_ASSIGNED", "TASK_DEADLINE_1H", "SCHEDULE_CREATED", "SCHEDULE_REMINDER_1H", "GENERAL"
    public String priority; // "LOW", "MEDIUM", "HIGH", "URGENT"
    public boolean is_read;
    public Date created_at;
    public Date read_at;

    // Default constructor
    public Notification() {
        this.is_read = false;
        this.created_at = new Date();
    }


    @Ignore
    public Notification(int user_id, Integer task_id, String title, String message, String type, String priority) {
        this.user_id = user_id;
        this.task_id = task_id;
        this.schedule_id = null;
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.is_read = false;
        this.created_at = new Date();
    }


    @Ignore
    public Notification(int user_id, Integer schedule_id, String title, String message, String type, String priority, boolean isSchedule) {
        this.user_id = user_id;
        this.task_id = null;
        this.schedule_id = schedule_id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.is_read = false;
        this.created_at = new Date();
    }


    @Ignore
    public Notification(int user_id, String title, String message, String type, String priority) {
        this.user_id = user_id;
        this.task_id = null;
        this.schedule_id = null;
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.is_read = false;
        this.created_at = new Date();
    }
} 