package com.fptu.fevent.repository;

import android.app.Application;
import androidx.lifecycle.LiveData; // Thêm import này

import com.fptu.fevent.database.AppDatabase; // Thêm import này
import com.fptu.fevent.model.User;
import com.fptu.fevent.dao.UserDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final LiveData<List<User>> allUsers; // Thêm thuộc tính này

    public UserRepository(Application application) {
        // Sửa lại dòng này để không bị dài
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
        allUsers = userDao.getAll(); // Khởi tạo allUsers từ DAO
    }

    /**
     * Lấy tất cả người dùng trong hệ thống.
     * Dữ liệu được bọc trong LiveData để UI có thể quan sát.
     */
    public LiveData<List<User>> getAll() {
        return allUsers;
    }

    /**
     * Lấy danh sách người dùng thuộc về một ban cụ thể.
     * @param teamId ID của ban cần lấy danh sách thành viên.
     * @return LiveData chứa danh sách các thành viên của ban.
     */
    public LiveData<List<User>> getUsersInTeam(int teamId) {
        return userDao.getUsersInTeam(teamId);
    }

    // THÊM PHƯƠNG THỨC MỚI NÀY
    public LiveData<List<User>> getUnassignedUsers() {
        return userDao.getUnassignedUsers();
    }

    /**
     * Chèn một người dùng mới vào database trên một luồng nền.
     */
    public void insert(User entity) {
        executor.execute(() -> userDao.insert(entity));
    }

    /**
     * Cập nhật thông tin một người dùng trên một luồng nền.
     */
    public void update(User entity) {
        executor.execute(() -> userDao.update(entity));
    }

    /**
     * Xóa một người dùng khỏi database trên một luồng nền.
     */
    public void delete(User entity) {
        executor.execute(() -> userDao.delete(entity));
    }
}