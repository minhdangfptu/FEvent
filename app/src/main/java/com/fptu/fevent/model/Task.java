package com.fptu.fevent.model;

import androidx.room.*;

import com.fptu.fevent.util.DateConverter;

import java.util.Date;

@Entity(
        tableName = "Task",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "assigned_to",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = Team.class,
                        parentColumns = "id",
                        childColumns = "team_id",
                        onDelete = ForeignKey.SET_NULL
                )
        }
)
@TypeConverters(DateConverter.class)
public class Task {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public String status;
    public Date due_date;

    @ColumnInfo(index = true)
    public Integer assigned_to;

    @ColumnInfo(index = true)
    public Integer team_id;
}
