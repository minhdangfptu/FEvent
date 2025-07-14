package com.fptu.fevent.model;

import androidx.room.*;

import com.fptu.fevent.util.DateConverter;

import java.util.Date;

@Entity(
        tableName = "EventFeedback",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
@TypeConverters(DateConverter.class)
public class EventFeedback {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(index = true)
    public int user_id;
    public int eventId;
    public int rating;
    public String comment;
    public Date created_at;
}
