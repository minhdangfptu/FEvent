package com.fptu.fevent.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Team")
public class Team {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String description;

    public Team(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
