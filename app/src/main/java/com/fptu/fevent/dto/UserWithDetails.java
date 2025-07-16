package com.fptu.fevent.dto;

import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;

import java.util.Date;

@TypeConverters()
public class UserWithDetails {
    public int id;
    public String name;
    public String email;
    public String fullname;

    @ColumnInfo(name = "date_of_birth")
    public Date date_of_birth;

    public String phone_number;
    public String club;
    public String department;
    public String position;

    @ColumnInfo(name = "team_name")
    public String teamName;

    @ColumnInfo(name = "role_name")
    public String roleName;

    public String image;
}
