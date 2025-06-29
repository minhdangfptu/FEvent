package com.fptu.fevent.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.fptu.fevent.util.PermissionConverter;
import java.util.List;

@Entity(tableName = "Role")
@TypeConverters(PermissionConverter.class)
public class Role {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String roleName;      // "admin", "member", "leader_ban, leader"
    public String description;

    public List<String> permissions;  // sử dụng TypeConverter
}
