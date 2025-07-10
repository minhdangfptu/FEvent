package com.fptu.fevent.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.fptu.fevent.util.IntegerListConverter;
import com.fptu.fevent.util.PermissionConverter;
import java.util.List;

@Entity(tableName = "Role")
@TypeConverters(IntegerListConverter.class)
public class Role {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String roleName;      // "admin", "member", "leader_ban, leader"
    public String description;

    public List<Integer> permissionIds;  // sử dụng TypeConverter

    public Role(int id, String roleName, String description, List<Integer> permissionIds) {
        this.id = id;
        this.roleName = roleName;
        this.description = description;
        this.permissionIds = permissionIds;
    }

    @Ignore
    public Role() {

    }
}
