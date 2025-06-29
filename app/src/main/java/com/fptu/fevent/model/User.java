package com.fptu.fevent.model;

import androidx.room.*;

@Entity(
        tableName = "User",
        foreignKeys = {
                @ForeignKey(
                        entity = Role.class,
                        parentColumns = "id",
                        childColumns = "role_id",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = Team.class,
                        parentColumns = "id",
                        childColumns = "team_id",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = EventInfo.class,
                        parentColumns = "id",
                        childColumns = "evenInfo_id;",
                        onDelete = ForeignKey.SET_NULL
                )
        }
)
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String email;
    public String password;
    public String fullname;
    public String phone_number;
    public String club;
    public String department;

    @ColumnInfo(index = true)
    public Integer role_id;

    @ColumnInfo(index = true)
    public Integer team_id;

    @ColumnInfo(index = true)
    public Integer evenInfo_id;
}
