package com.fptu.fevent.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Team")
public class Team {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String description;
}
