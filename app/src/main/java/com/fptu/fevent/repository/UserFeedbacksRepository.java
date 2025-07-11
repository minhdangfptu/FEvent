// File: com/fptu/fevent/repository/UserFeedbacksRepository.java
package com.fptu.fevent.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.fptu.fevent.model.UserFeedback;
import com.fptu.fevent.dao.UserFeedbackDao;
import com.fptu.fevent.database.AppDatabase;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserFeedbacksRepository {
    private final UserFeedbackDao userFeedbackDao;
    private final LiveData<List<UserFeedback>> allFeedbacks;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserFeedbacksRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userFeedbackDao = database.userFeedbackDao();
        allFeedbacks = userFeedbackDao.getAll();
    }

    public LiveData<List<UserFeedback>> getAll() {
        return allFeedbacks;
    }

    public void insert(UserFeedback entity) {
        executor.execute(() -> userFeedbackDao.insert(entity));
    }
}