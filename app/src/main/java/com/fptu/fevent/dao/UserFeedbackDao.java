
package com.fptu.fevent.dao;

import androidx.room.*;

        import com.fptu.fevent.model.Team;
import com.fptu.fevent.model.UserFeedback;

import java.util.List;

@Dao
public interface UserFeedbackDao {
    @Insert
    void insert(UserFeedback entity);

    @Update
    void update(UserFeedback entity);

    @Delete
    void delete(UserFeedback entity);

    @Query("SELECT * FROM UserFeedback")
    List<UserFeedback> getAll();

    @Insert
    void insertAll(UserFeedback... entities);

    @Query("SELECT COUNT(*) FROM UserFeedback")
    int getCount();

    @Query("SELECT * FROM UserFeedback WHERE to_user_id = :userId ORDER BY created_at DESC")
    List<UserFeedback> getFeedbackForUser(int userId);

    @Query("SELECT uf.*, u1.fullname as from_user_name, u2.fullname as to_user_name " +
            "FROM UserFeedback uf " +
            "LEFT JOIN User u1 ON uf.from_user_id = u1.id " +
            "LEFT JOIN User u2 ON uf.to_user_id = u2.id " +
            "ORDER BY uf.created_at DESC")
    List<UserFeedbackWithNames> getAllFeedbackWithNames();

    class UserFeedbackWithNames {
        public int id;
        public int from_user_id;
        public int to_user_id;
        public int rating;
        public String comment;
        public java.util.Date created_at;
        public String from_user_name;
        public String to_user_name;
    }
}
