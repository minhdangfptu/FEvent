package com.fptu.fevent.model;

import androidx.room.*;
import com.fptu.fevent.util.DateConverter;
import java.util.Date;

@Entity(
        tableName = "EventInfo",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "created_by",
                onDelete = ForeignKey.SET_NULL
        )
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

    public EventInfo() {
    }
    public EventInfo(int id, String name, Date start_time, Date end_time, String location, String description, Integer created_by) {
        this.id = id;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.location = location;
        this.description = description;
        this.created_by = created_by;
    }
}
