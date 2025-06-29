package com.fptu.fevent.model;

import androidx.room.*;

import com.fptu.fevent.util.DateConverter;

import java.util.Date;

@Entity(
        tableName = "EventInfo",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "created_by",
                        onDelete = ForeignKey.SET_NULL
                )
        }
)
@TypeConverters(DateConverter.class)
public class EventInfo {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public Date start_time;
    public Date end_time;
    public String location;
    public String description;

    @ColumnInfo(index = true)
    public Integer created_by;
}
