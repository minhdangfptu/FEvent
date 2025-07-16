package com.fptu.fevent.model;

import androidx.room.*;
import com.fptu.fevent.util.DateConverter;
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
@TypeConverters(DateConverter.class)
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

    public Schedule( String title, Date start_time, Date end_time, String location, String description, Integer team_id) {
<<<<<<< Updated upstream
        this.id = id;
        this.team_id = team_id;
=======

>>>>>>> Stashed changes
        this.title = title;
        this.start_time = start_time;
        this.end_time = end_time;
        this.location = location;
        this.description = description;
<<<<<<< Updated upstream
    }
    @Ignore
    public Schedule() {
=======
        this.team_id = team_id;
>>>>>>> Stashed changes
    }
}
