
package com.fptu.fevent.repository;

import android.app.Application;
import androidx.lifecycle.LiveData; // Thêm import
import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.dao.EventInfoDao;
import com.fptu.fevent.database.AppDatabase; // Thêm import
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventInfoRepository {
    private final EventInfoDao eventInfoDao;
    private final LiveData<List<EventInfo>> allEvents; // Thêm
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public EventInfoRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        eventInfoDao = database.eventInfoDao();
        allEvents = eventInfoDao.getAll(); // Thêm
    }

    public LiveData<List<EventInfo>> getAll() { // Sửa
        return allEvents;
    }

    public void insert(EventInfo entity) {
        executor.execute(() -> eventInfoDao.insert(entity));
    }

    public void update(EventInfo entity) {
        executor.execute(() -> eventInfoDao.update(entity));
    }

    public void delete(EventInfo entity) {
        executor.execute(() -> eventInfoDao.delete(entity));
    }
}