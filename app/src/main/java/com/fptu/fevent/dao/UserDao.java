package com.fptu.fevent.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.fptu.fevent.dto.UserWithDetails;
import com.fptu.fevent.model.Team;
import com.fptu.fevent.model.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Dao
public interface UserDao {
    @Insert
    long insert(User entity);

    @Update
    void update(User entity);

    @Delete
    void delete(User entity);

    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User WHERE email = :email AND password = :password AND (deactivated_until IS NULL OR deactivated_until < :now) LIMIT 1")
    User login(String email, String password, Date now);

    @Query("SELECT COUNT(*) FROM User WHERE email = :email")
    int countByEmail(String email);


    @Insert
    void insertAll(User... entities);

    @Query("SELECT COUNT(*) FROM User")
    int getCount();
    @Query("SELECT * FROM User WHERE id = :userId LIMIT 1")
    User getById(int userId);

    @Query("SELECT u.id, u.name, u.email, u.fullname, u.date_of_birth, u.phone_number, " +
            "u.club, u.department, u.position, " +
            "t.name AS team_name, r.roleName AS role_name " +
            "FROM User u " +
            "LEFT JOIN Team t ON u.team_id = t.id " +
            "LEFT JOIN Role r ON u.role_id = r.id " +
            "WHERE u.id = :userId LIMIT 1")
    UserWithDetails getUserDetailsWithNames(int userId);

    @Query("UPDATE User SET name = :name, email = :email, fullname = :fullname, date_of_birth = :dob, phone_number = :phoneNum, club = :club, department = :department, position = :position WHERE id = :id")
    void updateUserById(int id, String name, String email, String fullname, Date dob,String phoneNum, String club, String department, String position);

    @Query("UPDATE User SET deactivated_until = :until WHERE id = :userId")
    void deactivateUser(int userId, Date until);

    @Query("SELECT * FROM User WHERE id = :userId AND (deactivated_until IS NULL OR deactivated_until < :today)")
    LiveData<User> getActiveUser(int userId, Date today);

    @Query("DELETE FROM User WHERE id = :userId")
    void deleteById(int userId);

    @Query("UPDATE User SET password = :newPassword WHERE email = :email")
    void updatePassword(String email, String newPassword);

}
