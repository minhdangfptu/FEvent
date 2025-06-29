package com.fptu.fevent.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Permission") // Sử dụng snake_case cho chuẩn REST và DB
public class Permission {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;         // Mã quyền, ví dụ: create_task
    public String description;  // Mô tả quyền, ví dụ: "Tạo nhiệm vụ mới"
    public String group;        // Nhóm chức năng, ví dụ: TASK, SCHEDULE

}
