// File mới: com/fptu/fevent/viewmodel/EventInfoViewModel.java
package com.fptu.fevent.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.repository.EventInfoRepository;
import java.util.List;

public class EventInfoViewModel extends AndroidViewModel {
    private final EventInfoRepository repository;
    private final LiveData<List<EventInfo>> allEvents;

    public EventInfoViewModel(@NonNull Application application) {
        super(application);
        repository = new EventInfoRepository(application);
        allEvents = repository.getAll();
    }

    public void insert(EventInfo eventInfo) {
        repository.insert(eventInfo);
    }

    public void update(EventInfo eventInfo) {
        repository.update(eventInfo);
    }

    public void delete(EventInfo eventInfo) {
        repository.delete(eventInfo);
    }

    public LiveData<List<EventInfo>> getAllEvents() {
        return allEvents;
    }
}