package com.fptu.fevent.model;

import androidx.room.*;

import com.fptu.fevent.util.DateConverter;

import java.util.Date;

@Entity(
        tableName = "UserFeedback",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "from_user_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "to_user_id",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
@TypeConverters(DateConverter.class)
public class UserFeedback {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(index = true)
    public int from_user_id;

    @ColumnInfo(index = true)
    public int to_user_id;

    public int rating;
    public String comment;
    public Date created_at;
}
