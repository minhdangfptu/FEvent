package com.fptu.fevent.model;

import androidx.room.*;

import java.util.Date;

@Entity(
        tableName = "Schedule",
        foreignKeys = {
                @ForeignKey(
                        entity = Team.class,
                        parentColumns = "id",
                        childColumns = "team_id",
                        onDelete = ForeignKey.SET_NULL
                )
        }
)
public class Schedule {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(index = true)
    public Integer team_id;

    public String title;
    public Date start_time;
    public Date end_time;
    public String location;
    public String description;
}
