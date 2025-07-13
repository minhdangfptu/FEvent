package com.fptu.fevent.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.fptu.fevent.database.AppDatabase;
import com.fptu.fevent.dto.UserWithDetails;
import com.fptu.fevent.model.User;
import com.fptu.fevent.dao.UserDao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class UserRepository {
    private final UserDao userDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserRepository(Application application) {
        userDao = com.fptu.fevent.database.AppDatabase.getInstance(application).userDao();
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public User getUserById(int userId) {
        return userDao.getById(userId);
    }

    public void insert(User entity) {
        executor.execute(() -> userDao.insert(entity));
    }

    public void update(User entity) {
        executor.execute(() -> userDao.update(entity));
    }

    public void delete(User entity) {
        executor.execute(() -> userDao.delete(entity));
    }

    public interface LoginCallback {
        void onResult(User user);
    }

    public void login(String email, String password, LoginCallback callback) {
        executor.execute(() -> {
            Date now = new Date(); // Thời điểm hiện tại
            User user = userDao.login(email, password, now);
            callback.onResult(user);
        });
    }

    public interface CheckEmailCallback {
        void onResult(boolean exists);
    }

    /**
     * true = email đã tồn tại
     */
    public void isEmailExists(String email, CheckEmailCallback cb) {
        executor.execute(() -> {
            boolean exists = userDao.countByEmail(email) > 0;
            cb.onResult(exists);
        });
    }


    /**
     * thêm user xong trả về ID row ( >0 nếu OK )
     */
    public interface InsertCallback {
        void onInserted(long id);
    }

    public void insertAsync(User user, InsertCallback cb) {
        executor.execute(() -> {
            long newId = userDao.insert(user);
            cb.onInserted(newId);
        });
    }

    public interface GetUserCallback {
        void onResult(User user);
    }

    public void getUserById(int userId, GetUserCallback callback) {
        executor.execute(() -> {
            User user = userDao.getById(userId);
            callback.onResult(user);
        });
    }

    public interface UserDetailsCallback {
        void onResult(UserWithDetails userDetails);
    }

    public void getUserDetailsWithNames(int userId, UserDetailsCallback callback) {
        executor.execute(() -> {
            UserWithDetails result = userDao.getUserDetailsWithNames(userId);
            callback.onResult(result);
        });
    }

    public interface UpdateCallback {
        void onUpdated(boolean success);
    }

    public void updateUserById(int id, String name, String email, String fullname, Date dob, String phoneNum, String club, String department, String position, UpdateCallback cb) {
        executor.execute(() -> {
            try {
                userDao.updateUserById(id, name, email, fullname, dob, phoneNum, club, department, position);
                cb.onUpdated(true);
            } catch (Exception e) {
                e.printStackTrace();
                cb.onUpdated(false);
            }
        });
    }

    public void deactivateUser(int userId, Date until) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.deactivateUser(userId, until));
    }

    public LiveData<User> getActiveUser(int userId) {
        return userDao.getActiveUser(userId, new Date());
    }

    public interface DeleteCallback {
        void onResult(boolean success);
    }

    public void deleteUserById(int userId, DeleteCallback callback) {
        executor.execute(() -> {
            try {
                userDao.deleteById(userId);
                callback.onResult(true);
            } catch (Exception e) {
                callback.onResult(false);
            }
        });
    }
    public void updatePassword(String email, String newPassword, Runnable callback) {
        executor.execute(() -> {
            userDao.updatePassword(email, newPassword);
            if (callback != null) {
                callback.run();
            }
        });
    }
    public void updateEmail(String currentEmail, String newEmail, Runnable callback) {
        executor.execute(() -> {
            userDao.updateEmail(currentEmail, newEmail);
            if (callback != null) callback.run();
        });
    }
    public void getAllUsers(Consumer<List<User>> callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<User> users = userDao.getAll();
            callback.accept(users);
        });
    }
    public void searchUsers(String query, Consumer<List<User>> callback) {
        executor.execute(() -> {
            List<User> result = userDao.searchUsers(query);
            callback.accept(result);
        });
    }
    
    public List<User> getAllUsers() {
        return userDao.getAll();
    }
}
