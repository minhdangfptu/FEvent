package com.fptu.fevent.model;

import androidx.room.*;

import com.fptu.fevent.util.DateConverter;
import com.fptu.fevent.util.IntegerListConverter;

import java.util.Date;
import java.util.List;

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
                )
        }
)

@TypeConverters({DateConverter.class, IntegerListConverter.class})
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String email;
    public String password;
    public String fullname;
    public Date date_of_birth;
    public String phone_number;
    public String club;
    public String department;
    public String position;
    public Date deactivated_until;
    @ColumnInfo(index = true)
    public Integer role_id;


    @ColumnInfo(index = true)
    public Integer team_id;

    @Ignore
    public User() {

    }

    public User(String fullname, String email, String password) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
    }
    @Ignore
    public User(int id, String name, String email, String password, String fullname, Date date_of_birth, String phone_number, String club, String department, String position, Integer role_id, Integer team_id) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.date_of_birth = date_of_birth;
        this.phone_number = phone_number;
        this.club = club;
        this.department = department;
        this.position = position;
        this.role_id = role_id;
        this.team_id = team_id;

    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public Integer getRole_id() {
        return role_id;
    }

    public Integer getTeam_id() {
        return team_id;
    }

    public Date getDeactivated_until() {
        return deactivated_until;
    }
}
